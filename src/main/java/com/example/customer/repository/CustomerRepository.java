package com.example.customer.repository;

import com.example.customer.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByPhone(String phone);

    List<Customer> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM Customer c WHERE " +
           "(:name IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
           "(:phone IS NULL OR c.phone LIKE CONCAT('%', :phone, '%')) AND " +
           "(:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    Page<Customer> findCustomersWithFilters(@Param("name") String name,
                                          @Param("phone") String phone,
                                          @Param("email") String email,
                                          Pageable pageable);

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.phone = :phone AND (:id IS NULL OR c.id != :id)")
    long countByPhoneAndIdNot(@Param("phone") String phone, @Param("id") Long id);

    List<Customer> findByAgeGreaterThanEqual(Integer age);

    List<Customer> findByAgeLessThanEqual(Integer age);

    List<Customer> findByGender(Customer.Gender gender);
}