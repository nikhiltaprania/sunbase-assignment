package com.sunbaseassignment.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.model.CustomerAddress;
import com.sunbaseassignment.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the CustomerService interface for managing customer operations.
 *
 * @author Nikhil Kumar
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Saves a new customer and logs the operation.
     *
     * @param customer The customer object to be saved.
     * @return The saved customer.
     */
    @Override
    public Customer saveCustomer(Customer customer) {
        String uuid = UUID.randomUUID()
                .toString()
                .replaceAll("-", "");

        customer.setUuid("test" + uuid);

        Customer savedCustomer = customerRepository.save(customer);
        log.info("Customer saved: {}", savedCustomer);
        return savedCustomer;
    }

    /**
     * Retrieves a customer by their ID and logs the operation.
     *
     * @param uuid The ID of the customer to retrieve.
     * @return An optional containing the customer if found, otherwise empty.
     */
    @Override
    public Optional<Customer> getCustomerById(String uuid) {
        Optional<Customer> customer = customerRepository.findById(uuid);
        log.info("Customer fetched by ID {}: {}", uuid, customer);

        return customer;
    }

    /**
     * Retrieves a customer by their email and logs the operation.
     *
     * @param email The email of the customer to retrieve.
     * @return An optional containing the customer if found, otherwise empty.
     */
    @Override
    public Optional<Customer> getCustomerByEmail(String email) {
        Optional<Customer> customer = customerRepository.findByEmail(email);

        log.info("Customer fetched by email {}: {}", email, customer);
        return customer;
    }

    @Override
    public Customer getLoggedInCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Optional<Customer> loggedInCustomer = customerRepository.findByEmail(userDetails.getUsername());

            if (loggedInCustomer.isPresent()) {
                return loggedInCustomer.get();
            }
        }

        throw new RuntimeException("Unauthorized access");
    }

    /**
     * Deletes a customer by their ID and logs the operation.
     *
     * @param uuid The ID of the customer to delete.
     */
    @Override
    public void deleteCustomer(String uuid) {
        customerRepository.findById(uuid).ifPresent(customer -> {

            Customer loggedInCustomer = getLoggedInCustomer();
            if (loggedInCustomer.getUuid().equals(customer.getUuid()))
                throw new RuntimeException("Logged in customer cannot be deleted");

            customerRepository.delete(customer);
            log.info("Customer deleted by ID: {}", uuid);
        });
    }

    /**
     * Retrieves all customers and logs the operation.
     *
     * @return A list of all customers.
     */
    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();

        log.info("Fetched all customers. Total count: {}", customers.size());
        return customers;
    }

    /**
     * Retrieves customers page-wise based on the given page number and page size, and logs the operation.
     *
     * @param pageNo   The page number to retrieve.
     * @param pageSize The number of customers per page.
     * @return A list of customers for the given page.
     */
    @Override
    public List<Customer> getCustomerPageWise(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<Customer> customerPage = customerRepository.findAll(pageable);

        List<Customer> customers = customerPage.hasContent() ? customerPage.getContent() : Collections.emptyList();
        log.info("Fetched customers page-wise. Page number: {}, Page size: {}, Total count: {}", pageNo, pageSize, customers.size());
        return customers;
    }

    /**
     * Saves or updates customers in bulk and logs the operation.
     *
     * @param customers A list of customers to save or update.
     * @return A list of saved or updated customers.
     */
    @Override
    public List<Customer> saveOrUpdateInBulk(List<Customer> customers) {
        List<Customer> savedCustomers = new ArrayList<>();
        List<Customer> existingCustomers = customerRepository.findAll();
        existingCustomers.remove(getLoggedInCustomer());

        for (Customer newCustomer : customers) {
            boolean exists = existingCustomers.stream()
                    .anyMatch(existingCustomer -> twoCustomersAreEqual(newCustomer, existingCustomer));

            if (!exists) {
                savedCustomers.add(customerRepository.save(newCustomer));
            }
        }

        log.info("Bulk save/update completed. Number of new customers saved: {}", savedCustomers.size());
        return savedCustomers;
    }

    /**
     * Fetches customer data from an external server and logs the operation.
     *
     * @return A list of customers retrieved from the server.
     * @throws IOException If there is an error processing the response from the server.
     */
    @Override
    public List<Customer> fetchDataFromServer() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + getAccessToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange("https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list", HttpMethod.GET, entity, String.class);

        List<Map<String, Object>> rawList = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

        List<Customer> customers = rawList.stream().map(map -> {
            Customer customer = new Customer();
            customer.setUuid(map.get("uuid").toString());
            customer.setEmail(map.get("email").toString());
            customer.setFirstName(map.get("first_name").toString());
            customer.setLastName(map.get("last_name").toString());
            customer.setPhone(map.get("phone").toString());

            CustomerAddress address = new CustomerAddress();
            address.setStreet(map.get("street").toString());
            address.setAddress(map.get("address").toString());
            address.setCity(map.get("city").toString());
            address.setState(map.get("state").toString());

            customer.setCustomerAddress(address);
            return customer;

        }).collect(Collectors.toList());

        log.info("Fetched data from server. Number of customers: {}", customers.size());
        return customers;
    }

    /**
     * Retrieves an access token from the external server.
     *
     * @return The access token.
     */
    private String getAccessToken() {
        String url = "https://qa.sunbasedata.com/sunbase/portal/api/assignment_auth.jsp";

        Map<String, String> requestData = new HashMap<>();
        requestData.put("login_id", "test@sunbasedata.com");
        requestData.put("password", "Test@123");

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Accept", "application/json");

        HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(requestData, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                String accessToken = objectMapper.readTree(response.getBody()).path("access_token").asText();

                log.info("Access token retrieved successfully.");
                return accessToken;

            } else {
                throw new RuntimeException("HTTP error! Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            log.error("Error retrieving access token: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Compares two customers to determine if they are equal based on their attributes.
     *
     * @param c1 The first customer.
     * @param c2 The second customer.
     * @return True if the customers are considered equal, otherwise false.
     */
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