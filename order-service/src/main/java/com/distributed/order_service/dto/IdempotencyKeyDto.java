package com.distributed.order_service.dto;


import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyKeyDto {

    private UUID id;
    private String idempotencyKey;
    private String requestHash;
    private String response;
}
