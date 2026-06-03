package com.kaninnyc.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AnalyticsRepositoryTest {

    @Autowired
    AnalyticsRepository repository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp(){
        jdbcTemplate.update("call set_known_good_state();");
    }

    @Test
    void shouldFindNumOfOrdersOnlyAtDate() {
        LocalDate date = LocalDate.of(2026, 6, 1);
        Integer actual = repository.findNumOfOrdersAtDate(date);

        assertEquals(2, actual);
    }

    @Test
    void shouldFindTotalSalesOfCompletedOrders(){
        BigDecimal actual = repository.totalSales();

        assertEquals(new BigDecimal("12.00"), actual);
    }
}