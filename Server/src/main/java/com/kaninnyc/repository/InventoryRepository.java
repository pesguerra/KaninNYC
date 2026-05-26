package com.kaninnyc.repository;

import com.kaninnyc.model.InventoryItem;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class InventoryRepository {
    private final JdbcTemplate jdbcTemplate;

    public InventoryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InventoryItem> findAll() {
        return jdbcTemplate.query(
                "select id, name, quantity, unit, notes from inventory order by name",
                this::mapInventoryItem
        );
    }

    public Optional<InventoryItem> findById(Integer id) {
        List<InventoryItem> items = jdbcTemplate.query(
                "select id, name, quantity, unit, notes from inventory where id = ?",
                this::mapInventoryItem,
                id
        );
        return items.stream().findFirst();
    }

    public InventoryItem save(InventoryItem item) {
        if (item.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(
                        "insert into inventory (name, quantity, unit, notes) values (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setString(1, item.getName());
                statement.setInt(2, item.getQuantity());
                statement.setString(3, item.getUnit());
                statement.setString(4, item.getNotes());
                return statement;
            }, keyHolder);
            item.setId(keyHolder.getKey().intValue());
            return findById(item.getId()).orElse(item);
        }

        jdbcTemplate.update(
                "update inventory set name = ?, quantity = ?, unit = ?, notes = ? where id = ?",
                item.getName(),
                item.getQuantity(),
                item.getUnit(),
                item.getNotes(),
                item.getId()
        );
        return findById(item.getId()).orElse(item);
    }

    public void deleteById(Integer id) {
        jdbcTemplate.update("delete from inventory where id = ?", id);
    }

    private InventoryItem mapInventoryItem(ResultSet rs, int rowNum) throws SQLException {
        InventoryItem item = new InventoryItem();
        item.setId(rs.getInt("id"));
        item.setName(rs.getString("name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setUnit(rs.getString("unit"));
        item.setNotes(rs.getString("notes"));
        return item;
    }
}
