package ru.job4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {

}
