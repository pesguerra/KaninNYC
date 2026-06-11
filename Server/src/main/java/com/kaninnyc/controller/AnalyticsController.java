package com.kaninnyc.controller;

import com.kaninnyc.dto.AnalyticsDtos.AnalyticsResponse;
import com.kaninnyc.model.UserRole;
import com.kaninnyc.service.AnalyticsService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/analytics")
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final AuthorizationHelper authorizationHelper;

    public AnalyticsController(AnalyticsService analyticsService, AuthorizationHelper authorizationHelper) {
        this.analyticsService = analyticsService;
        this.authorizationHelper = authorizationHelper;
    }

    @GetMapping
    public ResponseEntity<Object> analytics(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.ADMIN);
        if (unauthorized != null) {
            return unauthorized;
        }

        return ResponseEntity.ok(new AnalyticsResponse(
                analyticsService.findTodaysNumOfOrders(),
                analyticsService.totalSales()
        ));
    }
}
