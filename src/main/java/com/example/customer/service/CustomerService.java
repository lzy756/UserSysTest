package com.example.customer.service;

import com.example.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    Customer saveCustomer(Customer customer);

    Customer updateCustomer(Customer customer);

    void deleteCustomer(Long id);

    Optional<Customer> findCustomerById(Long id);

    List<Customer> findAllCustomers();

    Page<Customer> findCustomersWithPagination(Pageable pageable);

    Page<Customer> findCustomersWithFilters(String name, String phone, String email, Pageable pageable);

    Optional<Customer> findCustomerByPhone(String phone);

    List<Customer> findCustomersByName(String name);

    List<Customer> findCustomersByAgeRange(Integer minAge, Integer maxAge);

    List<Customer> findCustomersByGender(Customer.Gender gender);

    boolean isPhoneExists(String phone, Long excludeId);

    long getTotalCustomerCount();
}