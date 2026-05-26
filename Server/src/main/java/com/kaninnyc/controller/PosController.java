package com.kaninnyc.controller;

import com.kaninnyc.dto.OrderDtos.CheckoutSessionResponse;
import com.kaninnyc.dto.OrderDtos.CreateOrderRequest;
import com.kaninnyc.model.UserRole;
import com.kaninnyc.service.MenuService;
import com.kaninnyc.service.OrderService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pos")
public class PosController {
    private final MenuService menuService;
    private final OrderService orderService;
    private final AuthorizationHelper authorizationHelper;

    public PosController(MenuService menuService, OrderService orderService, AuthorizationHelper authorizationHelper) {
        this.menuService = menuService;
        this.orderService = orderService;
        this.authorizationHelper = authorizationHelper;
    }

    @GetMapping("/menu")
    public ResponseEntity<Object> menu(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CASHIER);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(menuService.menuItems());
    }

    @GetMapping("/orders")
    public ResponseEntity<Object> orders(@RequestHeader Map<String, String> headers) {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CASHIER);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(orderService.allOrders());
    }

    @PostMapping("/orders")
    public ResponseEntity<Object> createOrder(
            @Valid @RequestBody CreateOrderRequest request,
            @RequestHeader Map<String, String> headers
    ) throws Exception {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CASHIER);
        if (unauthorized != null) {
            return unauthorized;
        }
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    @GetMapping("/checkout-session/{sessionId}")
    public ResponseEntity<Object> checkoutSession(
            @PathVariable String sessionId,
            @RequestHeader Map<String, String> headers
    ) throws Exception {
        ResponseEntity<Object> unauthorized = authorizationHelper.requireRole(headers, UserRole.CASHIER);
        if (unauthorized != null) {
            return unauthorized;
        }
        CheckoutSessionResponse response = orderService.checkoutSessionStatus(sessionId);
        return ResponseEntity.ok(response);
    }
}
