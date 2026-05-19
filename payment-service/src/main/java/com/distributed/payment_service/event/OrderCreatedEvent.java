package com.distributed.payment_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreatedEvent {

    private UUID id;
    private UUID eventId;
    private UUID userId;
    private BigDecimal totalAmount;
    private List<OrderItemEvent> items;

}
