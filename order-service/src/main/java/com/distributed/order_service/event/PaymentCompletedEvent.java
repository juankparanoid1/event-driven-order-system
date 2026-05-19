package com.distributed.order_service.event;

import com.distributed.order_service.dto.PaymentProvider;
import com.distributed.order_service.dto.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCompletedEvent {
    private UUID paymentId;
    private UUID orderId;
    private BigDecimal amount;
    private PaymentStatus status;
    private PaymentProvider provider;
    private String transactionReference;
}
