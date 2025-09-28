package com.example.customer.service.impl;

import com.example.customer.entity.Customer;
import com.example.customer.repository.CustomerRepository;
import com.example.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        if (isPhoneExists(customer.getPhone(), null)) {
            throw new RuntimeException("手机号已存在: " + customer.getPhone());
        }
        return customerRepository.save(customer);
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if (customer.getId() == null) {
            throw new RuntimeException("更新客户时ID不能为空");
        }

        if (isPhoneExists(customer.getPhone(), customer.getId())) {
            throw new RuntimeException("手机号已存在: " + customer.getPhone());
        }

        if (!customerRepository.existsById(customer.getId())) {
            throw new RuntimeException("客户不存在, ID: " + customer.getId());
        }

        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new RuntimeException("客户不存在, ID: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findCustomersWithPagination(Pageable pageable) {
        return customerRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Customer> findCustomersWithFilters(String name, String phone, String email, Pageable pageable) {
        return customerRepository.findCustomersWithFilters(
            name != null && !name.trim().isEmpty() ? name.trim() : null,
            phone != null && !phone.trim().isEmpty() ? phone.trim() : null,
            email != null && !email.trim().isEmpty() ? email.trim() : null,
            pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> findCustomerByPhone(String phone) {
        return customerRepository.findByPhone(phone);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findCustomersByName(String name) {
        return customerRepository.findByNameContainingIgnoreCase(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findCustomersByAgeRange(Integer minAge, Integer maxAge) {
        List<Customer> result = customerRepository.findAll();

        if (minAge != null) {
            result = customerRepository.findByAgeGreaterThanEqual(minAge);
        }

        if (maxAge != null) {
            if (minAge != null) {
                result = result.stream()
                    .filter(customer -> customer.getAge() != null && customer.getAge() <= maxAge)
                    .collect(java.util.stream.Collectors.toList());
            } else {
                result = customerRepository.findByAgeLessThanEqual(maxAge);
            }
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> findCustomersByGender(Customer.Gender gender) {
        return customerRepository.findByGender(gender);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneExists(String phone, Long excludeId) {
        return customerRepository.countByPhoneAndIdNot(phone, excludeId) > 0;
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalCustomerCount() {
        return customerRepository.count();
    }
}