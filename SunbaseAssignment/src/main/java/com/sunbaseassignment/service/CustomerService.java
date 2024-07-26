package com.sunbaseassignment.service;

import com.sunbaseassignment.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    Customer saveCustomer(Customer customer);

    Optional<Customer> getCustomerById(Integer customerId);

    Optional<Customer> getCustomerByEmail(String email);

    void deleteCustomer(Integer customerId);

    List<Customer> getAllCustomers();

    List<Customer> getCustomerPageWise(Integer pageNo, Integer pageSize);

    List<Customer> saveOrUpdateInBulk(List<Customer> customers);
}