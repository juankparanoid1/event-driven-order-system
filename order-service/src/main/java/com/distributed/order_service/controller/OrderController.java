package com.distributed.order_service.controller;

import com.distributed.order_service.dto.IdempotencyKeyDto;
import com.distributed.order_service.dto.OrderDto;
import com.distributed.order_service.service.IdempotencyKeyService;
import com.distributed.order_service.service.OrderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final IdempotencyKeyService idempotencyKeyService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    public ResponseEntity<OrderDto> createOrder(
            @RequestBody OrderDto orderDto,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        OrderDto order = null;
        try {
            String body = objectMapper.writeValueAsString(orderDto);
            String requestHash = DigestUtils.sha256Hex(body);
            OrderDto existingOrder = idempotencyKeyService.validateOrderIdempotencyKey(
                    idempotencyKey,
                    requestHash);
            if(existingOrder != null) {
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(existingOrder);
            }
            order =  orderService.createOrder(orderDto);
            idempotencyKeyService.saveIdempotencyKey(IdempotencyKeyDto
                    .builder()
                    .idempotencyKey(idempotencyKey)
                    .requestHash(requestHash)
                    .response(objectMapper.writeValueAsString(order))
                    .build()
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(order);
    }
}
