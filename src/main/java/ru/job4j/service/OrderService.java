package ru.job4j.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.*;
import ru.job4j.event.NotificationEvent;
import ru.job4j.event.OrderEvent;
import ru.job4j.messaging.EventPublisher;
import ru.job4j.repository.CustomerRepository;
import ru.job4j.repository.DishMemRepository;
import ru.job4j.repository.OrderRepository;
import ru.job4j.requests.OrderRequest;
import ru.job4j.requests.PaymentRequest;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final DishMemRepository dishRepository;
    private final CustomerRepository customerRepository;
    private final PaymentServiceClient paymentServiceClient;
    private final EventPublisher eventPublisher;

    @Transactional
    public void createOrder(OrderRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found!"));

        PaymentRequest paymentRequest = PaymentRequest
                .builder()
                .accountId(request.getAccountId())
                .total(calculateTotalPrice(request.getDishes()))
                .build();

        Payment payment = paymentServiceClient.processPayment(paymentRequest);

        Order order = Order.builder()
                .customerId(request.getCustomerId())
                .dishes(request.getDishes())
                .paymentId(payment.getId())
                .status(OrderStatus.ORDER_CREATED)
                .build();

        orderRepository.save(order);
        OrderEvent event = OrderEvent
                .builder()
                .orderId(order.getId())
                .dishes(request.getDishes())
                .orderStatus(order.getStatus())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();

        try {
            eventPublisher.orderPLaced(event);
        } catch (Exception ex) {
            cancelOrder(order.getId());
            throw new RuntimeException("Kitchen service unavailable.");
        }
    }

    @Transactional
    public void cancelOrder(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found!"));
        paymentServiceClient.refundPayment(orderId);
        order.setStatus(OrderStatus.ORDER_CANCELLED);
        NotificationEvent notificationEvent = NotificationEvent.builder()
                .message("Kitchen service cannot precess your order. You've been fully refunded!")
                .email(customer.getEmail())
                .build();
        eventPublisher.newNotification(notificationEvent);
    }

    public Optional<Order> findOrder(Integer id) {
        return orderRepository.findById(id);
    }

    public OrderStatus checkOrderStatus(int orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        return order.getStatus();
    }

    @Transactional
    public void updateOrderStatus(int orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found!"));
        order.setStatus(status);
    }

    public double calculateTotalPrice(Map<Integer, Integer> dishes) {
        Map<Integer, Dish> dishMap = dishRepository.findAllDishes();
        return dishes.entrySet()
                .stream()
                .mapToDouble(entry -> entry.getValue() * dishMap.get(entry.getKey()).getCost())
                .sum();
    }

    public void updateDishMemRepository() {
        dishRepository.updateRepository();
    }
}
