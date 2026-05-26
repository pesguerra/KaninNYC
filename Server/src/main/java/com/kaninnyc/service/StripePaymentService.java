package com.kaninnyc.service;

import com.kaninnyc.model.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentService {
    private final String secretKey;
    private final String currency;
    private final String clientBaseUrl;

    public StripePaymentService(
            @Value("${app.stripe.secret-key}") String secretKey,
            @Value("${app.stripe.currency}") String currency,
            @Value("${app.client.base-url}") String clientBaseUrl
    ) {
        this.secretKey = secretKey;
        this.currency = currency;
        this.clientBaseUrl = clientBaseUrl;
    }

    public Session createCheckoutSession(Order order) throws StripeException {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("STRIPE_SECRET_KEY is required for card payments");
        }

        Stripe.apiKey = secretKey;
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(clientBaseUrl + "/pos/success?session_id={CHECKOUT_SESSION_ID}&order_id=" + order.getId())
                .setCancelUrl(clientBaseUrl + "/pos/cancel?order_id=" + order.getId())
                .putMetadata("orderId", order.getId().toString())
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(currency)
                                                .setUnitAmount(toSmallestCurrencyUnit(order.getTotal()))
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                .setName("Kanin NYC Order #" + order.getId())
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
        return Session.create(params);
    }

    public Session retrieveCheckoutSession(String sessionId) throws StripeException {
        if (secretKey == null || secretKey.isBlank()) {
            throw new IllegalStateException("STRIPE_SECRET_KEY is required to retrieve Stripe sessions");
        }

        Stripe.apiKey = secretKey;
        return Session.retrieve(sessionId);
    }

    private long toSmallestCurrencyUnit(BigDecimal amount) {
        return amount.multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP)
                .longValueExact();
    }
}
