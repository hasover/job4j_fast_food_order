package ru.job4j.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.job4j.event.OrderEvent;
import ru.job4j.service.OrderService;

@Service
@RequiredArgsConstructor
public class CancelledOrderEventHandler {
    private final OrderService orderService;

    @KafkaListener(topics = "${kafka.topic.cancelled-orders}")
    public void handleCancelledOrder(OrderEvent event) {
        orderService.cancelOrder(event.getOrderId());
    }
}
