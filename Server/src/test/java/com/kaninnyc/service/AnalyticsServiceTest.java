package com.kaninnyc.service;

import com.kaninnyc.repository.AnalyticsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class AnalyticsServiceTest {

    @Autowired
    AnalyticsService service;

    @MockitoBean
    AnalyticsRepository repository;

    @Test
    void findNumOfOrdersByDate() {
        LocalDate date = LocalDate.now();
        when(repository.findNumOfOrdersAtDate(date)).thenReturn(2);

        Integer actual = service.findTodaysNumOfOrders();

        assertEquals(2, actual);
    }

    @Test
    void shouldFindTotalSalesOfCompletedOrders(){
        when(repository.totalSales()).thenReturn(new BigDecimal("12.00"));

        BigDecimal actual = repository.totalSales();

        assertEquals(new BigDecimal("12.00"), actual);
    }
}