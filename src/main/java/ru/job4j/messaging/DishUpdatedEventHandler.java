package ru.job4j.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.job4j.service.OrderService;

@Service
@RequiredArgsConstructor
public class DishUpdatedEventHandler {
    private final OrderService orderService;

    @KafkaListener(topics = "${kafka.topic.dish-updates}",
            containerFactory = "dishKafkaListenerContainerFactory")
    public void handleDishUpdatedEvent(String message) {
        orderService.updateDishMemRepository();
    }
}
