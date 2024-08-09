package com.sunbaseassignment.service;

import com.sunbaseassignment.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service class for loading user details based on email.
 *
 * @author Nikhil Kumar
 */
@Service
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    /**
     * Constructor to initialize the CustomerRepository.
     *
     * @param customerRepository The repository for accessing customer data.
     */
    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    /**
     * Loads user details by email.
     *
     * @param email The email of the user to load.
     * @return UserDetails object if found.
     * @throws UsernameNotFoundException If the user with the given email is not found.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}