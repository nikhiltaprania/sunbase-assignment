package com.sunbaseassignment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling requests to view static pages such as login/signup and the home page.
 *
 * @author Nikhil Kumar
 */
@Controller
public class TemplateController {
    /**
     * Returns the view name for the login/signup page.
     *
     * @return The name of the view for the login/signup page.
     */
    @GetMapping("/login-signup")
    public String loginSignupPage() {
        return "login-signup";
    }

    /**
     * Returns the view name for the home page.
     *
     * @return The name of the view for the home page.
     */
    @GetMapping("/index")
    public String homePage() {
        return "index";
    }
}