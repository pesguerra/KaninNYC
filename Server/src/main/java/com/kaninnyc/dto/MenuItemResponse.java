package com.kaninnyc.dto;

import java.math.BigDecimal;

public record MenuItemResponse(
        Integer id,
        String name,
        BigDecimal price,
        String description
) {
}
