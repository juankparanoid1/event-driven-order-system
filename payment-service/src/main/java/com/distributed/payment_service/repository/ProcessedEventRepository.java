package com.distributed.payment_service.repository;

import com.distributed.payment_service.entity.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    boolean existsByEventIdAndServiceName(UUID eventId, String serviceName);
}
