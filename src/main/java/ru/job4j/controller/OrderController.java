package ru.job4j.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.job4j.Order;
import ru.job4j.OrderStatus;
import ru.job4j.requests.OrderRequest;
import ru.job4j.service.OrderService;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @GetMapping("/{orderId}")
    public Order findOrder(@PathVariable Integer orderId) {
        return orderService.findOrder(orderId)
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found!"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createOrder(@RequestBody OrderRequest request) {
        try {
            orderService.createOrder(request);
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ex.getMessage());
        }
    }

    @PostMapping("/{orderId}/status")
    public void updateOrderStatus(@PathVariable int orderId, @RequestBody OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
    }

    @GetMapping("/{orderId}/status")
    public OrderStatus getOrderStatus(@PathVariable int orderId) {
        return orderService.checkOrderStatus(orderId);
    }

}
