package com.kaninnyc.controller;

import com.kaninnyc.dto.OrderDtos.OrderResponse;
import com.kaninnyc.dto.OrderDtos.UpdateOrderStatusRequest;
import com.kaninnyc.model.UserRole;
import com.kaninnyc.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/kitchen")
public class KitchenController {
    private final OrderService orderService;
    private final AuthorizationHelper authorizationHelper;

    public KitchenController(OrderService orderService, AuthorizationHelper authorizationHelper) {
        this.orderService = orderService;
        this.authorizationHelper = authorizationHelper;
    }

    @GetMapping("/orders")
    public ResponseEntity<Object> activeOrders(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CHEF);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(orderService.activeKitchenOrders());
    }

    @GetMapping("/orders/done")
    public ResponseEntity<Object> doneOrders(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CHEF);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(orderService.doneKitchenOrders());
    }

    @PatchMapping("/orders/{orderId}/status")
    public ResponseEntity<Object> updateStatus(
            @PathVariable Integer orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            @RequestHeader Map<String, String> headers
    ) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CHEF);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(orderService.updateStatus(orderId, request.status()));
    }
}
