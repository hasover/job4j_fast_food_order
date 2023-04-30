package ru.job4j.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.job4j.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
}
