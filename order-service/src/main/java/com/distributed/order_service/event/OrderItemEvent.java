package com.distributed.order_service.event;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {

    private UUID id;
    private BigDecimal price;
    private UUID orderId;
    private Integer quantity;
    private UUID productId;

}
