package com.sunbaseassignment.controller;

import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.service.CustomerService;
import com.sunbaseassignment.util.Response;
import com.sunbaseassignment.util.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;

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
        return customerService.getCustomerById(customerId).map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK)).orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
    }

    @GetMapping("/getByEmail")
    public Response<Customer> getCustomerByEmail(@RequestParam String email) {
        return customerService.getCustomerByEmail(email).map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK)).orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
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
            List<Customer> savedOrUpdatedCustomers = customerService.saveOrUpdateInBulk(customerService.fetchDataFromServer());
            return new Response<>(savedOrUpdatedCustomers, "Updated in bulk successfully", StatusCode.CREATED);
        } catch (IOException e) {
            return new Response<>(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}