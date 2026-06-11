package com.kaninnyc.service;

import com.kaninnyc.dto.OrderDtos.CreateOrderRequest;
import com.kaninnyc.dto.OrderDtos.CheckoutSessionResponse;
import com.kaninnyc.dto.OrderDtos.OrderResponse;
import com.kaninnyc.model.MenuItem;
import com.kaninnyc.model.Order;
import com.kaninnyc.model.OrderItem;
import com.kaninnyc.model.OrderStatus;
import com.kaninnyc.model.Payment;
import com.kaninnyc.model.PaymentMethod;
import com.kaninnyc.model.PaymentStatus;
import com.kaninnyc.repository.MenuItemRepository;
import com.kaninnyc.repository.OrderRepository;
import com.kaninnyc.repository.PaymentRepository;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;
    private final PaymentRepository paymentRepository;
    private final StripePaymentService stripePaymentService;
    private final OrderMapper orderMapper;

    public OrderService(
            OrderRepository orderRepository,
            MenuItemRepository menuItemRepository,
            PaymentRepository paymentRepository,
            StripePaymentService stripePaymentService,
            OrderMapper orderMapper
    ) {
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.paymentRepository = paymentRepository;
        this.stripePaymentService = stripePaymentService;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public OrderResponse createOrder(CreateOrderRequest request) throws Exception {
        Order order = new Order();
        order.setName(request.name());
        order.setPaymentMethod(request.paymentMethod());
        order.setNotes(request.notes());

        BigDecimal total = BigDecimal.ZERO;
        for (var itemRequest : request.items()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.menuItemId())
                    .orElseThrow(() -> new IllegalArgumentException("Menu item not found: " + itemRequest.menuItemId()));
            OrderItem orderItem = new OrderItem();
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.quantity());
            order.addItem(orderItem);
            total = total.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.quantity())));
        }
        order.setTotal(total);

        Order saved = orderRepository.save(order);

        if (saved.getPaymentMethod() == PaymentMethod.CARD) {
            Payment payment = createPayment(saved);
            Session session = stripePaymentService.createCheckoutSession(saved);
            payment.setStripeCheckoutSessionId(session.getId());
            Payment savedPayment = paymentRepository.save(payment);
            return orderMapper.toResponse(saved, savedPayment, session.getUrl());
        }

        return orderMapper.toResponse(saved);
    }

    @Transactional
    public CheckoutSessionResponse checkoutSessionStatus(String sessionId) throws Exception {
        Session session = stripePaymentService.retrieveCheckoutSession(sessionId);
        Payment payment = paymentRepository.findByStripeCheckoutSessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found for Stripe checkout session"));

        if ("paid".equals(session.getPaymentStatus())) {
            payment.setStatus(PaymentStatus.SUCCEEDED);
            paymentRepository.save(payment);
        }

        return new CheckoutSessionResponse(
                session.getId(),
                session.getStatus(),
                session.getPaymentStatus(),
                payment.getOrder().getId(),
                payment.getStatus()
        );
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> allOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> ordersByUserId(Integer user_id) {
        return orderRepository.findByUserId(user_id).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> activeKitchenOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtAsc(
                        List.of(OrderStatus.RECEIVED, OrderStatus.IN_PROGRESS, OrderStatus.READY)
                ).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> doneKitchenOrders() {
        return orderRepository.findByStatusInOrderByCreatedAtDesc(List.of(OrderStatus.COMPLETED)).stream()
                .map(orderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateStatus(Integer orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        order.setStatus(status);
        return orderMapper.toResponse(orderRepository.save(order));
    }

    private Payment createPayment(Order order) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(order.getPaymentMethod());
        payment.setAmount(order.getTotal());
        payment.setStatus(PaymentStatus.PENDING);
        return payment;
    }
}
