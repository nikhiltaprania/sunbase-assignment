package com.sunbaseassignment.service;

import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.repository.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        customerRepository.findById(customerId).ifPresent(customerRepository::delete);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public List<Customer> getCustomerPageWise(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Customer> customerPage = customerRepository.findAll(pageable);
        if (customerPage.hasContent()) {
            return customerPage.getContent();
        }
        return null;
    }

    @Override
    public List<Customer> saveOrUpdateInBulk(List<Customer> customers) {
        List<Customer> savedCustomers = new ArrayList<>();
        List<Customer> existingCustomers = customerRepository.findAll();

        for (Customer newCustomer : customers) {
            boolean exists = false;
            for (Customer existingCustomer : existingCustomers) {
                if (twoCustomersAreEqual(newCustomer, existingCustomer)) {
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                savedCustomers.add(customerRepository.save(newCustomer));
            }
        }
        return savedCustomers;
    }

    private boolean twoCustomersAreEqual(Customer c1, Customer c2) {
        return c1.getFirstName().equals(c2.getFirstName()) &&
                c1.getLastName().equals(c2.getLastName()) &&
                c1.getEmail().equals(c2.getEmail()) &&
                c1.getPhone().equals(c2.getPhone()) &&
                c1.getCustomerAddress().getAddress().equals(c2.getCustomerAddress().getAddress()) &&
                c1.getCustomerAddress().getCity().equals(c2.getCustomerAddress().getCity()) &&
                c1.getCustomerAddress().getState().equals(c2.getCustomerAddress().getState()) &&
                c1.getCustomerAddress().getStreet().equals(c2.getCustomerAddress().getStreet());
    }
}