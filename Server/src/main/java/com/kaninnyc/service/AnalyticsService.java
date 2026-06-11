package com.kaninnyc.service;

import com.kaninnyc.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public Integer findTodaysNumOfOrders() {
        LocalDate date = LocalDate.now();
        return repository.findNumOfOrdersAtDate(date);
    }

    public BigDecimal shouldFindTotalSalesOfCompletedOrders() {
        return repository.totalSales();
    }

    public BigDecimal totalSales () {
        BigDecimal total = repository.totalSales();
        return total == null ? BigDecimal.ZERO : total;
    }
}
