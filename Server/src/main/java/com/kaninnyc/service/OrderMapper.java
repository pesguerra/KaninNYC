package com.kaninnyc.service;

import com.kaninnyc.dto.OrderDtos.OrderItemResponse;
import com.kaninnyc.dto.OrderDtos.OrderResponse;
import com.kaninnyc.dto.OrderDtos.PaymentResponse;
import com.kaninnyc.model.Order;
import com.kaninnyc.model.OrderItem;
import com.kaninnyc.model.Payment;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order, Payment payment, String stripeCheckoutUrl) {
        PaymentResponse paymentResponse = payment == null ? null : new PaymentResponse(
                payment.getId(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getAmount(),
                payment.getStripeCheckoutSessionId(),
                stripeCheckoutUrl
        );

        return new OrderResponse(
                order.getId(),
                order.getName(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getTotal(),
                order.getCreatedAt(),
                order.getNotes(),
                order.getItems().stream().map(this::toItemResponse).toList(),
                paymentResponse
        );
    }

    public OrderResponse toResponse(Order order) {
        return toResponse(order, null, null);
    }

    private OrderItemResponse toItemResponse(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getMenuItem().getId(),
                item.getMenuItem().getName(),
                item.getMenuItem().getPrice(),
                item.getQuantity()
        );
    }
}
