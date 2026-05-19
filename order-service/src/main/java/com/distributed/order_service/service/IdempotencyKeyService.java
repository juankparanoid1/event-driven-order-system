package com.distributed.order_service.service;

import com.distributed.order_service.dto.IdempotencyKeyDto;
import com.distributed.order_service.dto.OrderDto;
import com.distributed.order_service.entity.IdempotencyKey;
import com.distributed.order_service.repository.IdempotencyKeyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class IdempotencyKeyService {

    private final IdempotencyKeyRepository idempotencyKeyRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrderDto validateOrderIdempotencyKey(
            String idempotencyKey,
            String requestHash
    ) throws JsonProcessingException {
        Optional<IdempotencyKey> order = idempotencyKeyRepository
                .findByIdempotencyKey(idempotencyKey);

        if(order.isPresent()) {
            if(!order.get().getRequestHash().equals(requestHash)) {
                throw new RuntimeException(
                        "Idempotency key reused with different payload"
                );
            }
            return objectMapper.readValue(order.get().getResponse(), OrderDto.class);
        }
        return null;
    }

    @Transactional
    public IdempotencyKeyDto  saveIdempotencyKey (IdempotencyKeyDto idempotencyKey) {
        IdempotencyKey key = idempotencyKeyRepository.save(IdempotencyKey.builder()
                .idempotencyKey(idempotencyKey.getIdempotencyKey())
                .requestHash(idempotencyKey.getRequestHash())
                .response(idempotencyKey.getResponse())
                .build());
        return IdempotencyKeyDto
                .builder()
                .id(key.getId())
                .idempotencyKey(key.getIdempotencyKey())
                .requestHash(key.getRequestHash())
                .response(key.getResponse())
                .build();
    }

    @Transactional
    public void cleanIdempotencyKeys() {
        LocalDateTime expiration = LocalDateTime.now().minusDays(7);
        int deleted = idempotencyKeyRepository.deleteExpiredKeys(expiration);
        log.info("Deleted {} expired idempotency keys", deleted);
    }
}
