package ru.job4j.repository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;
import ru.job4j.Dish;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DishMemRepository {
    private final RestTemplate restTemplate;
    private final Map<Integer, Dish> dishMap = new ConcurrentHashMap<>();
    private final String url;

    public DishMemRepository(RestTemplateBuilder builder,
                             @Value("${dishes.api.url}") String url) {
        this.restTemplate = builder.build();
        this.url = url;
    }

    public Map<Integer, Dish> findAllDishes() {
        return dishMap;
    }

    @PostConstruct
    public void updateRepository() {
        dishMap.clear();
        List<Dish> body = restTemplate.exchange(url, HttpMethod.GET, null,
                new ParameterizedTypeReference<List<Dish>>() {
                }).getBody();
        if (body != null) {
            body.forEach(d -> dishMap.put(d.getId(), d));
        }
    }
}
