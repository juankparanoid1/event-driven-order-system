package com.distributed.order_service.service;

import com.distributed.order_service.dto.OrderDto;
import com.distributed.order_service.dto.OrderItemDto;
import com.distributed.order_service.dto.OrderStatus;
import com.distributed.order_service.entity.Order;
import com.distributed.order_service.entity.OrderItem;
import com.distributed.order_service.entity.OutboxEvents;
import com.distributed.order_service.event.OrderCreatedEvent;
import com.distributed.order_service.event.OrderItemEvent;
import com.distributed.order_service.repository.OrderItemRepository;
import com.distributed.order_service.repository.OrderRepository;
import com.distributed.order_service.repository.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public OrderDto createOrder(OrderDto orderDto) {
        Order order = orderRepository.save(Order.builder()
                .userId(orderDto.getUserId())
                .status(OrderStatus.CREATED)
                .totalAmount(orderDto.getTotalAmount())
                .build());

        List<OrderItem> orderItem = orderItemRepository.saveAll(
                orderDto.getOrderItems()
                        .stream()
                        .map(item -> {
                            return OrderItem.builder()
                                    .orderId(order.getId())
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build();
                        })
                        .toList()
        );

        List<OrderItemDto> orderItemDtos = orderItem
                .stream()
                .map(item -> {
                    return OrderItemDto.builder()
                            .id(item.getId())
                            .orderId(item.getOrderId())
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .price(item.getPrice())
                            .build();
                }).toList();

        OrderDto orderResponse = OrderDto.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .totalAmount(order.getTotalAmount())
                .orderItems(orderItemDtos)
                .build();

        OrderCreatedEvent orderCreatedEvent = OrderCreatedEvent.builder()
                .id(orderResponse.getId())
                .eventId(UUID.randomUUID())
                .userId(orderResponse.getUserId())
                .totalAmount(orderResponse.getTotalAmount())
                .items(orderResponse.getOrderItems()
                        .stream()
                        .map(item -> {
                            return OrderItemEvent.builder()
                                    .id(item.getId())
                                    .orderId(item.getOrderId())
                                    .productId(item.getProductId())
                                    .quantity(item.getQuantity())
                                    .price(item.getPrice())
                                    .build();
                        }).toList()
                )
                .build();

        publishEvent(orderCreatedEvent);

        return orderResponse;
    }

    private void publishEvent(OrderCreatedEvent orderCreatedEvent) {
        try {
            OutboxEvents outboxEvents = OutboxEvents
                    .builder()
                    .aggregateId(orderCreatedEvent.getId())
                    .aggregateType("ORDER")
                    .eventType("ORDER_CREATED")
                    .payload(objectMapper.writeValueAsString(orderCreatedEvent))
                    .processed(false)
                    .build();
            outboxEventRepository.save(outboxEvents);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
