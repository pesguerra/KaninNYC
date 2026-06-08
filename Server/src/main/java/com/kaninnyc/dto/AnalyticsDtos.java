package com.kaninnyc.dto;

import java.math.BigDecimal;

public final class AnalyticsDtos {
    private AnalyticsDtos() {
    }

    public record AnalyticsResponse(
            Integer ordersToday,
            BigDecimal totalSales
    ) {
    }
}
