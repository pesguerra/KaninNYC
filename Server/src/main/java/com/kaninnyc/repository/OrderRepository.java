package com.kaninnyc.repository;

import com.kaninnyc.model.MenuItem;
import com.kaninnyc.model.Order;
import com.kaninnyc.model.OrderItem;
import com.kaninnyc.model.OrderStatus;
import com.kaninnyc.model.PaymentMethod;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepository {
    private final JdbcTemplate jdbcTemplate;

    public OrderRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Order> findById(Integer id) {
        List<Order> orders = jdbcTemplate.query(
                "select id, name, status, payment_method, total, created_at, notes from orders where id = ?",
                this::mapOrder,
                id
        );
        return orders.stream().findFirst();
    }

    public List<Order> findByStatusInOrderByCreatedAtAsc(Collection<OrderStatus> statuses) {
        String placeholders = statuses.stream().map(status -> "?").reduce((left, right) -> left + "," + right).orElse("?");
        Object[] params = statuses.stream().map(Enum::name).toArray();
        List<Order> orders = jdbcTemplate.query(
                "select id, name, status, payment_method, total, created_at, notes from orders where status in ("
                        + placeholders + ") order by created_at asc",
                this::mapOrder,
                params
        );
        attachItems(orders);
        return orders;
    }

    public List<Order> findByStatusInOrderByCreatedAtDesc(Collection<OrderStatus> statuses) {
        String placeholders = statuses.stream().map(status -> "?").reduce((left, right) -> left + "," + right).orElse("?");
        Object[] params = statuses.stream().map(Enum::name).toArray();
        List<Order> orders = jdbcTemplate.query(
                "select id, name, status, payment_method, total, created_at, notes from orders where status in ("
                        + placeholders + ") order by created_at desc",
                this::mapOrder,
                params
        );
        attachItems(orders);
        return orders;
    }

    public List<Order> findAllByOrderByCreatedAtDesc() {
        List<Order> orders = jdbcTemplate.query(
                "select id, name, status, payment_method, total, created_at, notes from orders order by created_at desc",
                this::mapOrder
        );
        attachItems(orders);
        return orders;
    }

    public Order save(Order order) {
        if (order.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(
                        "insert into orders (name, status, payment_method, total, notes) values (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, order.getName());
                statement.setString(2, order.getStatus().name());
                statement.setString(3, order.getPaymentMethod().name());
                statement.setBigDecimal(4, order.getTotal());
                statement.setString(5, order.getNotes());
                return statement;
            }, keyHolder);
            order.setId(keyHolder.getKey().intValue());
            insertItems(order);
            return findById(order.getId()).map(saved -> {
                saved.setItems(order.getItems());
                return saved;
            }).orElse(order);
        }

        jdbcTemplate.update(
                "update orders set name = ?, status = ?, payment_method = ?, total = ?, notes = ? where id = ?",
                order.getName(),
                order.getStatus().name(),
                order.getPaymentMethod().name(),
                order.getTotal(),
                order.getNotes(),
                order.getId()
        );
        return findById(order.getId()).orElse(order);
    }

    private void insertItems(Order order) {
        for (OrderItem item : order.getItems()) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(
                        "insert into order_items (order_id, menu_item, quantity) values (?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setInt(1, order.getId());
                statement.setInt(2, item.getMenuItem().getId());
                statement.setInt(3, item.getQuantity());
                return statement;
            }, keyHolder);
            item.setId(keyHolder.getKey().intValue());
        }
    }

    private void attachItems(List<Order> orders) {
        for (Order order : orders) {
            order.setItems(findItemsForOrder(order));
        }
    }

    private List<OrderItem> findItemsForOrder(Order order) {
        return jdbcTemplate.query(
                """
                select oi.id as order_item_id, oi.quantity,
                       mi.id as menu_item_id, mi.name, mi.price, mi.description
                from order_items oi
                join menu_items mi on mi.id = oi.menu_item
                where oi.order_id = ?
                order by oi.id
                """,
                (rs, rowNum) -> mapOrderItem(rs, order),
                order.getId()
        );
    }

    private Order mapOrder(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("id"));
        order.setName(rs.getString("name"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        order.setPaymentMethod(PaymentMethod.valueOf(rs.getString("payment_method")));
        order.setTotal(rs.getBigDecimal("total"));
        order.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        order.setNotes(rs.getString("notes"));
        return order;
    }

    private OrderItem mapOrderItem(ResultSet rs, Order order) throws SQLException {
        MenuItem menuItem = new MenuItem();
        menuItem.setId(rs.getInt("menu_item_id"));
        menuItem.setName(rs.getString("name"));
        menuItem.setPrice(rs.getBigDecimal("price"));
        menuItem.setDescription(rs.getString("description"));

        OrderItem item = new OrderItem();
        item.setId(rs.getInt("order_item_id"));
        item.setOrder(order);
        item.setMenuItem(menuItem);
        item.setQuantity(rs.getInt("quantity"));
        return item;
    }
}
