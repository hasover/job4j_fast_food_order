package ru.job4j.messaging;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFutureCallback;
import ru.job4j.event.NotificationEvent;
import ru.job4j.event.OrderEvent;

@Service
@Slf4j
public class EventPublisher {
    private final String ordersTopic;
    private final String notificationsTopic;
    private final KafkaTemplate<Integer, Object> kafkaTemplate;

    public EventPublisher(@Value("${kafka.topic.orders}") String ordersTopic,
                          @Value("${kafka.topic.notifications}") String notificationsTopic,
                          KafkaTemplate<Integer, Object> kafkaTemplate) {
        this.ordersTopic = ordersTopic;
        this.notificationsTopic = notificationsTopic;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void orderPLaced(OrderEvent event) {
        kafkaTemplate.send(ordersTopic, event);

        NotificationEvent notificationEvent = NotificationEvent
                .builder()
                .message(String.format("Order %d has been successfully placed!",
                        event.getOrderId()))
                .email(event.getEmail())
                .build();
        newNotification(notificationEvent);

    }

    public void newNotification(NotificationEvent event) {
        var future = kafkaTemplate.send(notificationsTopic, event);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable ex) {
                log.error("Notification service unavailable:{}", ex.getMessage());
            }

            @Override
            public void onSuccess(SendResult<Integer, Object> result) {

            }
        });

    }
}
