package com.kaninnyc.service;

import com.kaninnyc.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AnalyticsService {

    private final AnalyticsRepository repository;

    public AnalyticsService(AnalyticsRepository repository) {
        this.repository = repository;
    }

    public Integer findTodaysNumOfOrders(){
        LocalDate date = LocalDate.now();
        return repository.findNumOfOrdersAtDate(date);
    }
}
