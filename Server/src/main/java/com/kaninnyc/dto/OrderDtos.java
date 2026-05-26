package com.kaninnyc.dto;

import com.kaninnyc.model.OrderStatus;
import com.kaninnyc.model.PaymentMethod;
import com.kaninnyc.model.PaymentStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class OrderDtos {
    private OrderDtos() {
    }

    public record CreateOrderRequest(
            @NotBlank String name,
            @NotNull PaymentMethod paymentMethod,
            String notes,
            @NotEmpty List<@Valid CreateOrderItemRequest> items
    ) {
    }

    public record CreateOrderItemRequest(
            @NotNull Integer menuItemId,
            @Min(1) int quantity
    ) {
    }

    public record OrderResponse(
            Integer id,
            String name,
            OrderStatus status,
            PaymentMethod paymentMethod,
            BigDecimal total,
            LocalDateTime createdAt,
            String notes,
            List<OrderItemResponse> items,
            PaymentResponse payment
    ) {
    }

    public record OrderItemResponse(
            Integer id,
            Integer menuItemId,
            String menuItemName,
            BigDecimal unitPrice,
            int quantity
    ) {
    }

    public record PaymentResponse(
            Integer id,
            PaymentMethod paymentMethod,
            PaymentStatus status,
            BigDecimal amount,
            String stripeCheckoutSessionId,
            String stripeCheckoutUrl
    ) {
    }

    public record CheckoutSessionResponse(
            String id,
            String status,
            String paymentStatus,
            Integer orderId,
            PaymentStatus localPaymentStatus
    ) {
    }

    public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {
    }
}
