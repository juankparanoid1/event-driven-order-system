package com.distributed.order_service.dto;

public enum OrderStatus {
    CREATED,
    PENDING_PAYMENT,
    PAYMENT_FAILED,
    PAID,
    INVENTORY_RESERVED,
    CONFIRMED,
    CANCELLED
}
