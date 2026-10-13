package com.loanapp.loan_application.service.customer;

import com.loanapp.loan_application.dto.customer.CustomerProfileResponse;

public interface CustomerService {
    CustomerProfileResponse findByProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response);
}