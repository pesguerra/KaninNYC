package com.kaninnyc.repository;

import com.kaninnyc.model.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class AnalyticsRepository {
    private final JdbcTemplate jdbcTemplate;

    public AnalyticsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Integer findNumOfOrdersAtDate(LocalDate date){
        return jdbcTemplate.queryForObject(
                """
                SELECT COUNT(*)
                FROM orders
                WHERE created_at LIKE CONCAT(?, '%');
                """,
                Integer.class,
                date
        );
    }
}
