package com.kaninnyc.repository;

import com.kaninnyc.model.Order;
import com.kaninnyc.model.Payment;
import com.kaninnyc.model.PaymentStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {
    private final JdbcTemplate jdbcTemplate;
    private static final String PAYMENT_SELECT = """
            select id, order_id, status, amount,
                   stripe_checkout_session_id, stripe_payment_intent_id, stripe_charge_id, created_at
            from payments
            """;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<Payment> findById(Integer id) {
        List<Payment> payments = jdbcTemplate.query(
                PAYMENT_SELECT + " where id = ?",
                this::mapPayment,
                id
        );
        return payments.stream().findFirst();
    }

    public Optional<Payment> findByStripeCheckoutSessionId(String stripeCheckoutSessionId) {
        List<Payment> payments = jdbcTemplate.query(
                PAYMENT_SELECT + " where stripe_checkout_session_id = ?",
                this::mapPayment,
                stripeCheckoutSessionId
        );
        return payments.stream().findFirst();
    }

    public Payment save(Payment payment) {
        if (payment.getId() == null) {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                var statement = connection.prepareStatement(
                        """
                        insert into payments
                            (order_id, status, amount, stripe_checkout_session_id, stripe_payment_intent_id, stripe_charge_id)
                        values (?, ?, ?, ?, ?, ?)
                        """,
                        Statement.RETURN_GENERATED_KEYS
                );
                statement.setInt(1, payment.getOrder().getId());
                statement.setString(2, payment.getStatus().name());
                statement.setBigDecimal(3, payment.getAmount());
                statement.setString(4, payment.getStripeCheckoutSessionId());
                statement.setString(5, payment.getStripePaymentIntentId());
                statement.setString(6, payment.getStripeChargeId());
                return statement;
            }, keyHolder);
            payment.setId(keyHolder.getKey().intValue());
            return findById(payment.getId()).orElse(payment);
        }

        jdbcTemplate.update(
                """
                update payments
                set order_id = ?, status = ?, amount = ?, stripe_checkout_session_id = ?,
                    stripe_payment_intent_id = ?, stripe_charge_id = ?
                where id = ?
                """,
                payment.getOrder().getId(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getStripeCheckoutSessionId(),
                payment.getStripePaymentIntentId(),
                payment.getStripeChargeId(),
                payment.getId()
        );
        return findById(payment.getId()).orElse(payment);
    }

    private Payment mapPayment(ResultSet rs, int rowNum) throws SQLException {
        Order order = new Order();
        order.setId(rs.getInt("order_id"));

        Payment payment = new Payment();
        payment.setId(rs.getInt("id"));
        payment.setOrder(order);
        payment.setStatus(PaymentStatus.valueOf(rs.getString("status")));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setStripeCheckoutSessionId(rs.getString("stripe_checkout_session_id"));
        payment.setStripePaymentIntentId(rs.getString("stripe_payment_intent_id"));
        payment.setStripeChargeId(rs.getString("stripe_charge_id"));
        payment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return payment;
    }
}
