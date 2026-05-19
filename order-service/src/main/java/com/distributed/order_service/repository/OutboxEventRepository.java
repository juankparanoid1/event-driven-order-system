package com.distributed.order_service.repository;

import com.distributed.order_service.entity.OutboxEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvents, UUID> {

    List<OutboxEvents> findByProcessedFalseAndAggregateType(String aggregateType);
}
