package com.distributed.payment_service.event.producer;

import com.distributed.payment_service.entity.OutboxEvents;
import com.distributed.payment_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOrderPayment() {
        List<OutboxEvents> outboxEvents = outboxEventRepository.findByProcessedFalseAndAggregateType("PAYMENT");

        for(OutboxEvents event: outboxEvents) {
            publish(event);
            event.setProcessed(Boolean.TRUE);
        }
        outboxEventRepository.saveAll(outboxEvents);
    }

    private void publish(OutboxEvents event) {
        kafkaTemplate.send(
                    "payment.events",
                    event.getAggregateId().toString(),
                    event.getPayload()
            );
    }
}
