package com.sunbaseassignment.controller;

import com.sunbaseassignment.jwt.JwtUtil;
import com.sunbaseassignment.model.Customer;
import com.sunbaseassignment.service.CustomerService;
import com.sunbaseassignment.util.Response;
import com.sunbaseassignment.util.StatusCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for handling authentication-related operations such as registration and login.
 *
 * @author Nikhil Kumar
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {
    private final CustomerService customerService;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Handles the registration of a new customer.
     *
     * @param customer The customer object containing registration details.
     * @return A response object containing the registered customer or an error message.
     */
    @PostMapping("/register")
    public Response<?> register(@RequestBody Customer customer) {
        if (customer.getEmail() != null && !customer.getEmail().isEmpty() && customerService.getCustomerByEmail(customer.getEmail()).isPresent())
            return new Response<>("Account already exists !", StatusCode.CONFLICT);

        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        Customer savedCustomer = customerService.saveCustomer(customer);
        return new Response<>(savedCustomer, "Registration Successful", StatusCode.CREATED);
    }

    /**
     * Handles the login process for a customer.
     *
     * @param customer The customer object containing login details.
     * @return A response object containing a success message and JWT token, or an error message.
     */
    @PostMapping("/login")
    public Response<?> login(@RequestBody Customer customer) {
        try {
            UserDetails userDetails = userDetailsService.loadUserByUsername(customer.getEmail());

            if (!passwordEncoder.matches(customer.getPassword(), userDetails.getPassword()))
                return new Response<>("Password is incorrect", StatusCode.UNAUTHORIZED);
            if (!userDetails.isEnabled())
                return new Response<>("Account is disabled !", StatusCode.UNAUTHORIZED);
            if (!userDetails.isAccountNonLocked())
                return new Response<>("Account is locked !", StatusCode.UNAUTHORIZED);
            if (!userDetails.isAccountNonExpired())
                return new Response<>("Account is expired !", StatusCode.UNAUTHORIZED);
            if (!userDetails.isCredentialsNonExpired())
                return new Response<>("Account credentials are expired !", StatusCode.UNAUTHORIZED);

            Authentication authentication = new UsernamePasswordAuthenticationToken(customer.getEmail(), customer.getPassword(), userDetails.getAuthorities());
            authenticationManager.authenticate(authentication);

            String jwtToken = jwtUtil.generateToken(userDetails);
            return new Response<>("Login Successful", StatusCode.OK, jwtToken);

        } catch (Exception e) {
            return new Response<>(e.getMessage(), StatusCode.UNAUTHORIZED);
        }
    }
}