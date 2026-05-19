package com.distributed.order_service.event.producer;

import com.distributed.order_service.entity.OutboxEvents;
import com.distributed.order_service.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
@RequiredArgsConstructor
public class OrderEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishOrderCreated() {
        List<OutboxEvents> outboxEvents = outboxEventRepository.findByProcessedFalseAndAggregateType("ORDER");

        for(OutboxEvents event: outboxEvents) {
            publish(event);
            event.setProcessed(Boolean.TRUE);
        }
        outboxEventRepository.saveAll(outboxEvents);
    }

    private void publish(OutboxEvents event) {
            kafkaTemplate.send(
                    "orders.events",
                    event.getAggregateId().toString(),
                    event.getPayload()
            );
    }
}
