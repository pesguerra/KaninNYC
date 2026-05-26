package com.kaninnyc.repository;

import com.kaninnyc.model.MenuItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class MenuItemRepository {
    private final JdbcTemplate jdbcTemplate;

    public MenuItemRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<MenuItem> findById(Integer id) {
        List<MenuItem> items = jdbcTemplate.query(
                "select id, name, price, description from menu_items where id = ?",
                this::mapMenuItem,
                id
        );
        return items.stream().findFirst();
    }

    public List<MenuItem> findAll() {
        return jdbcTemplate.query(
                "select id, name, price, description from menu_items order by id",
                this::mapMenuItem
        );
    }

    private MenuItem mapMenuItem(ResultSet rs, int rowNum) throws SQLException {
        MenuItem item = new MenuItem();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setPrice(rs.getBigDecimal("price"));
        item.setDescription(rs.getString("description"));
        return item;
    }
}
