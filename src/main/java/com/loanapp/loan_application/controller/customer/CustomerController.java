package com.loanapp.loan_application.controller.customer;

import com.loanapp.loan_application.service.customer.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth/v1/customer/")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> customerDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> customerProfile(){

//        return  ResponseEntity


    }
}
