package com.distributed.payment_service.event.consumer;

import com.distributed.payment_service.dto.PaymentProvider;
import com.distributed.payment_service.dto.PaymentStatus;
import com.distributed.payment_service.entity.OutboxEvents;
import com.distributed.payment_service.entity.Payment;
import com.distributed.payment_service.entity.ProcessedEvent;
import com.distributed.payment_service.event.OrderCreatedEvent;
import com.distributed.payment_service.event.PaymentCompletedEvent;
import com.distributed.payment_service.repository.OutboxEventRepository;
import com.distributed.payment_service.repository.PaymentRepository;
import com.distributed.payment_service.repository.ProcessedEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final PaymentRepository paymentRepository;
    private final ObjectMapper objectMapper;
    private final ProcessedEventRepository processedEventRepository;
    private static final String SERVICE_NAME = "payment-service";
    private final OutboxEventRepository outboxEventRepository;

    @Transactional
    @KafkaListener(topics = "orders.events", groupId = "payment-group")
    public void consumeOrderCreated(String payload) {
        log.info("Processing order {}", payload);

        try {
            OrderCreatedEvent order = objectMapper.readValue(
                    payload,
                    OrderCreatedEvent.class
            );

            boolean processed = processedEventRepository.existsByEventIdAndServiceName(
                    order.getEventId(),
                    SERVICE_NAME
            );

            if(processed) {
                return;
            }

            Payment payment = Payment.builder()
                    .amount(order.getTotalAmount())
                    .orderId(order.getId())
                    .provider(PaymentProvider.STRIPE)
                    .status(PaymentStatus.PAYMENT_COMPLETED)
                    .transactionReference(UUID.randomUUID().toString())
                    .build();
            paymentRepository.save(payment);
            OutboxEvents outboxEvents = publishEvent(payment);
            outboxEventRepository.save(outboxEvents);

            processedEventRepository.save(
                    ProcessedEvent.builder()
                            .eventId(order.getEventId())
                            .serviceName(SERVICE_NAME)
                            .build()
            );
        } catch (Exception e) {
            log.error("FAILED processing message: {}", payload, e);
            throw new RuntimeException(e);
        }
    }

    private OutboxEvents publishEvent(Payment payment) {
        try {
            PaymentCompletedEvent paymentCompletedEvent = PaymentCompletedEvent.builder()
                    .paymentId(payment.getId())
                    .orderId(payment.getOrderId())
                    .amount(payment.getAmount())
                    .status(payment.getStatus())
                    .provider(payment.getProvider())
                    .transactionReference(payment.getTransactionReference())
                    .build();
            return OutboxEvents
                    .builder()
                    .aggregateId(payment.getId())
                    .aggregateType("PAYMENT")
                    .eventType(PaymentStatus.PAYMENT_COMPLETED.toString())
                    .payload(objectMapper.writeValueAsString(paymentCompletedEvent))
                    .processed(false)
                    .build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
