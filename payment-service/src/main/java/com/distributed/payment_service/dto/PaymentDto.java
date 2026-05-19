package com.distributed.payment_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDto {
    private UUID id;
    private BigDecimal amount;
    private UUID orderId;
    private String provider;
    private String status;
    private String transactionReference;
}
