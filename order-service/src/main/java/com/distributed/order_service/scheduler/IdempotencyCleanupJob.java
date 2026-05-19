package com.distributed.order_service.scheduler;

import com.distributed.order_service.repository.IdempotencyKeyRepository;
import com.distributed.order_service.service.IdempotencyKeyService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IdempotencyCleanupJob {

    private final IdempotencyKeyService idempotencyKeyService;

    @Scheduled(cron = "0 0 * * * *")
    @Transactional
    public void cleanIdempotencyKeys() {
        idempotencyKeyService.cleanIdempotencyKeys();
    }
}
