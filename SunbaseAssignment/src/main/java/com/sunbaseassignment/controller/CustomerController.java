package com.sunbaseassignment.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.model.CustomerAddress;
import com.sunbaseassignment.service.CustomerService;
import com.sunbaseassignment.util.Response;
import com.sunbaseassignment.util.StatusCode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String accessToken;

    public CustomerController(CustomerService customerService, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.customerService = customerService;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.accessToken = getAccessToken();
    }

    @PostMapping("/save")
    public Response<Customer> saveCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.saveCustomer(customer);
        return new Response<>(savedCustomer, "Customer saved successfully", StatusCode.CREATED);
    }

    @PutMapping("/update")
    public Response<Customer> updateCustomer(@RequestBody Customer customer) {
        if (customerService.getCustomerById(customer.getCustomerId()).isPresent()) {
            Customer updatedCustomer = customerService.saveCustomer(customer);
            return new Response<>(updatedCustomer, "Customer updated successfully", StatusCode.CREATED);
        }
        return new Response<>("Customer not found", StatusCode.NOT_FOUND);
    }

    @DeleteMapping("/delete")
    public Response<String> deleteCustomer(@RequestParam Integer customerId) {
        customerService.deleteCustomer(customerId);
        return new Response<>("Customer deleted successfully", StatusCode.OK);
    }

    @GetMapping("/getById")
    public Response<Customer> getCustomerById(@RequestParam Integer customerId) {
        return customerService.getCustomerById(customerId)
                .map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK))
                .orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
    }

    @GetMapping("/getByEmail")
    public Response<Customer> getCustomerByEmail(@RequestParam String email) {
        return customerService.getCustomerByEmail(email)
                .map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK))
                .orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
    }

    @GetMapping("/all")
    public Response<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("No customers found");
            return new Response<>("No Customers found !", StatusCode.NOT_FOUND);
        }

        return new Response<>(customers, "Customer fetched", StatusCode.OK);
    }

    @GetMapping("/current")
    public Response<?> getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails userDetails) {
            Optional<Customer> optionalCustomer = customerService.getCustomerByEmail(userDetails.getUsername());
            if (optionalCustomer.isPresent()) {
                return new Response<>(optionalCustomer.get(), "Customer found !", StatusCode.OK);
            }
        }

        return new Response<>("Customer not found !", StatusCode.UNAUTHORIZED);
    }

    @GetMapping("/getPageWise/{pageNo}/{pageSize}")
    public Response<List<Customer>> getCustomersPageWise(@PathVariable Integer pageNo, @PathVariable Integer pageSize) {
        System.out.println("pageNo: " + pageNo + ", pageSize: " + pageSize);
        List<Customer> customers = customerService.getCustomerPageWise(pageNo, pageSize);
        return new Response<>(customers, "Customers fetched page wise", StatusCode.OK);
    }

    @GetMapping("/sync")
    public Response<?> sync() {
        try {
            List<Customer> savedOrUpdatedCustomers = customerService.saveOrUpdateInBulk(fetchData());
            return new Response<>(savedOrUpdatedCustomers, "Updated in bulk successfully", StatusCode.CREATED);
        } catch (IOException e) {
            return new Response<>(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
        }
    }

    public List<Customer> fetchData() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Fetch the raw JSON response
        ResponseEntity<String> response = restTemplate
                .exchange("https://qa.sunbasedata.com/sunbase/portal/api/assignment.jsp?cmd=get_customer_list", HttpMethod.GET, entity, String.class);

        // Parse the raw JSON into a List of Maps
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> rawList = objectMapper.readValue(response.getBody(), new TypeReference<>() {});

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
        }).collect(Collectors.toList());
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
}