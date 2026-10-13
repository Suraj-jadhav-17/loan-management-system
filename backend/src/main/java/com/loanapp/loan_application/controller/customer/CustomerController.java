package com.loanapp.loan_application.controller.customer;

import com.loanapp.loan_application.dto.customer.CustomerProfileResponse;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.customer.CustomerService;
import com.loanapp.loan_application.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("auth/v1/customer/")
public class CustomerController {

    private final UserService userService;
    private final CustomerService customerService;

    public CustomerController(UserService userService, CustomerService customerService) {
        this.userService = userService;
        this.customerService = customerService;
    }

    @GetMapping("/dashboard")
    public ResponseEntity<?> customerDashboard() {
        return ResponseEntity.ok("Welcome to Customer Dashboard");
    }

    @GetMapping("/profile")
    public ResponseEntity<?> customerProfile(Authentication authentication, HttpServletRequest request) {

        String email = authentication.getName();
        CustomerProfileResponse user = customerService.findByProfile(email);


        return ResponseEntity.ok(new ApiResponse<>("200", true, "Success", user, LocalDateTime.now(), request.getRequestURI(), null));


    }

    @PutMapping("/profile")
    public ResponseEntity<?>updateProfile(Authentication authentication,HttpServletRequest request,    @RequestBody CustomerProfileResponse response){
        String email = authentication.getName();
        System.out.println(email);
        CustomerProfileResponse customer = customerService.updateProfile(email,response);
        return ResponseEntity.ok(new ApiResponse<>("200", true, "Success", customer, LocalDateTime.now(), request.getRequestURI(), null));



    }

}
