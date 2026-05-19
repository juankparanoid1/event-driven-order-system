package com.distributed.order_service.dto;

import com.distributed.order_service.entity.OrderItem;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {

    private UUID id;
    private UUID userId;
    private BigDecimal totalAmount;
    List<OrderItemDto> orderItems;
}
