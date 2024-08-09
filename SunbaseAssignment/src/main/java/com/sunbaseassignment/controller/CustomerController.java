package com.sunbaseassignment.controller;

import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.service.CustomerService;
import com.sunbaseassignment.util.Response;
import com.sunbaseassignment.util.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/**
 * Controller for handling customer-related operations such as saving, updating, deleting, and retrieving customers.
 *
 * @author Nikhil Kumar
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    /**
     * Saves a new customer.
     *
     * @param customer The customer object to be saved.
     * @return A response object containing the saved customer and a success message.
     */
    @PostMapping("/save")
    public Response<Customer> saveCustomer(@RequestBody Customer customer) {
        Customer savedCustomer = customerService.saveCustomer(customer);
        return new Response<>(savedCustomer, "Customer saved successfully", StatusCode.CREATED);
    }

    /**
     * Updates an existing customer.
     *
     * @param customer The customer object with updated details.
     * @return A response object containing the updated customer and a success message, or an error message if the customer is not found.
     */
    @PutMapping("/update")
    public Response<Customer> updateCustomer(@RequestBody Customer customer) {
        if (customerService.getCustomerById(customer.getUuid()).isPresent()) {
            Customer updatedCustomer = customerService.saveCustomer(customer);
            return new Response<>(updatedCustomer, "Customer updated successfully", StatusCode.CREATED);
        }

        return new Response<>("Customer not found", StatusCode.NOT_FOUND);
    }

    /**
     * Deletes a customer by their ID.
     *
     * @param uuid The ID of the customer to be deleted.
     * @return A response object containing a success message.
     */
    @DeleteMapping("/delete")
    public Response<String> deleteCustomer(@RequestParam String uuid) {
        try {
            customerService.deleteCustomer(uuid);
            return new Response<>("Customer deleted successfully", StatusCode.OK);

        } catch (RuntimeException e) {
            return new Response<>(e.getMessage(), StatusCode.BAD_REQUEST);
        }
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param uuid The ID of the customer to be retrieved.
     * @return A response object containing the customer and a success message, or an error message if the customer is not found.
     */
    @GetMapping("/getById")
    public Response<Customer> getCustomerById(@RequestParam String uuid) {
        return customerService.getCustomerById(uuid)
                .map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK))
                .orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
    }

    /**
     * Retrieves a customer by their email.
     *
     * @param email The email of the customer to be retrieved.
     * @return A response object containing the customer and a success message, or an error message if the customer is not found.
     */
    @GetMapping("/getByEmail")
    public Response<Customer> getCustomerByEmail(@RequestParam String email) {
        return customerService.getCustomerByEmail(email)
                .map(customer -> new Response<>(customer, "Customer fetched", StatusCode.OK))
                .orElse(new Response<>("Customer not found", StatusCode.NOT_FOUND));
    }

    /**
     * Retrieves all customers.
     *
     * @return A response object containing the list of all customers and a success message, or an error message if no customers are found.
     */
    @GetMapping("/all")
    public Response<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        if (customers.isEmpty())
            return new Response<>("No Customers found !", StatusCode.NOT_FOUND);

        return new Response<>(customers, "Customers fetched", StatusCode.OK);
    }

    /**
     * Retrieves the currently logged-in customer.
     *
     * @return A response object containing the logged-in customer and a success message, or an error message if the customer is not found.
     */
    @GetMapping("/current")
    public Response<?> getLoggedInUser() {
        try {
            Customer loggedInCustomer = customerService.getLoggedInCustomer();
            return new Response<>(loggedInCustomer, "Customer found !", StatusCode.OK);

        } catch (RuntimeException e) {
            return new Response<>(e.getMessage(), StatusCode.UNAUTHORIZED);
        }
    }

    /**
     * Retrieves customers page-wise based on the given page number and page size.
     *
     * @param pageNo   The page number to retrieve.
     * @param pageSize The number of customers per page.
     * @return A response object containing the list of customers for the given page and a success message.
     */
    @GetMapping("/getPageWise/{pageNo}/{pageSize}")
    public Response<List<Customer>> getCustomersPageWise(@PathVariable Integer pageNo, @PathVariable Integer pageSize) {
        List<Customer> customers = customerService.getCustomerPageWise(pageNo, pageSize);
        return new Response<>(customers, "Customers fetched page wise", StatusCode.OK);
    }

    /**
     * Synchronizes customers with data from an external server.
     *
     * @return A response object containing the list of saved or updated customers and a success message, or an error message if synchronization fails.
     */
    @GetMapping("/sync")
    public Response<?> syncCustomers() {
        try {
            List<Customer> savedOrUpdatedCustomers = customerService.saveOrUpdateInBulk(customerService.fetchDataFromServer());
            return new Response<>(savedOrUpdatedCustomers, "Updated in bulk successfully", StatusCode.CREATED);

        } catch (IOException e) {
            return new Response<>(e.getMessage(), StatusCode.INTERNAL_SERVER_ERROR);
        }
    }
}