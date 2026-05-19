package com.distributed.order_service.entity;

import com.distributed.order_service.dto.OrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(updatable = false, nullable = false)
    private UUID id;
    private UUID userId;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;
}
