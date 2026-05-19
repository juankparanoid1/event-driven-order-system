package com.distributed.payment_service.entity;

import com.distributed.payment_service.dto.PaymentProvider;
import com.distributed.payment_service.dto.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Payment {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    private BigDecimal amount;
    private UUID orderId;
    @Enumerated(EnumType.STRING)
    private PaymentProvider provider;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private String transactionReference;
    @CreatedDate
    @Column(nullable = false, updatable = false)
    protected LocalDateTime createdAt;
}
