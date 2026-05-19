package com.distributed.order_service.dto;

public enum PaymentProvider {
    STRIPE,
    PAYPAL,
    BANK_TRANSFER,
    MERCADO_PAGO,
    ADYEN
}
