package com.sunbaseassignment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.model.CustomerAddress;
import com.sunbaseassignment.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

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

    @Override
    public List<Customer> fetchDataFromServer() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Fetch the raw JSON response
        ResponseEntity<String> response = restTemplate.exchange("https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list", HttpMethod.GET, entity, String.class);

        // Parse the raw JSON into a List of Maps
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> rawList = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        // Convert the List of Maps into List of Customer objects
        return rawList.stream().map(map -> {
            Customer customer = new Customer();
            customer.setEmail((String) map.get("email"));
            customer.setFirstName((String) map.get("first_name"));
            customer.setLastName((String) map.get("last_name"));
            customer.setPhone((String) map.get("phone"));

            CustomerAddress address = new CustomerAddress();
            address.setStreet((String) map.get("street"));
            address.setAddress((String) map.get("address"));
            address.setCity((String) map.get("city"));
            address.setState((String) map.get("state"));

            customer.setCustomerAddress(address);
            return customer;
        }).toList();
    }

    private String getAccessToken() {
        String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

        // Set the request payload
        Map<String, String> requestData = new HashMap<>();
        requestData.put("login_id", "test@sunbasedata.com");
        requestData.put("password", "Test@123");

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        // Create request entity with payload and headers
        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

        try {
            // Make the POST request
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            // Parse the response body as JsonNode
            if (response.getStatusCode().is2xxSuccessful()) {
                return objectMapper.readTree(response.getBody()).path("access_token").asText();
            } else {
                throw new RuntimeException("HTTP error! Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            return null;
        }
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