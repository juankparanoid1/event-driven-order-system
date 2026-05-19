package com.distributed.order_service.event.consumer;

import com.distributed.order_service.dto.OrderStatus;
import com.distributed.order_service.dto.PaymentStatus;
import com.distributed.order_service.entity.Order;
import com.distributed.order_service.event.PaymentCompletedEvent;
import com.distributed.order_service.repository.OrderRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderEventConsumer {

    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    private static final Map<PaymentStatus, OrderStatus> PAYMENT_ORDER_STATUS = Map.of(
            PaymentStatus.PAYMENT_COMPLETED, OrderStatus.PAID,
            PaymentStatus.PAYMENT_FAILED, OrderStatus.PAYMENT_FAILED,
            PaymentStatus.PAYMENT_IN_PROGRESS, OrderStatus.PENDING_PAYMENT
    );

    @KafkaListener(topics = "payment.events", groupId = "orders-group")
    public void consumePayment(String payload) {
        log.info("Processing order paid {}", payload);
        try {
            PaymentCompletedEvent payment = objectMapper.readValue(payload, PaymentCompletedEvent.class);
            Order order = orderRepository.findById(payment.getOrderId()).orElseThrow();
            order.setStatus(PAYMENT_ORDER_STATUS.get(payment.getStatus()));
            orderRepository.save(order);
        } catch (JsonProcessingException e) {
            log.error("FAILED processing message: {}", payload, e);
            throw new RuntimeException(e);
        }
    }
}
