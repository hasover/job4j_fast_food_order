package ru.job4j.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.job4j.Payment;
import ru.job4j.requests.PaymentRequest;

@Service
public class PaymentServiceClient {
    private final RestTemplate restTemplate;
    private final String url;

    public PaymentServiceClient(RestTemplateBuilder builder,
                                @Value("${payments.api.url}") String url) {
        this.restTemplate = builder.build();
        this.url = url;
    }

    public Payment processPayment(PaymentRequest request) {
        Payment payment = restTemplate.postForEntity(
                url, request, Payment.class).getBody();
        if (payment == null) {
            throw new RuntimeException("Not enough money to pay!");
        }
        return payment;
    }

    public Payment refundPayment(int id) {
        Payment payment = restTemplate.exchange(
                String.format("%s/%d", url, id),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Payment.class).getBody();
        if (payment == null) {
            throw new RuntimeException("Payment does not exist!");
        }
        return payment;
    }
}
