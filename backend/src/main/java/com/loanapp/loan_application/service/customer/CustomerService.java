package com.loanapp.loan_application.service.customer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;

import java.util.List;


public interface CustomerService {
    CustomerProfileResponse findByProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response);

    List<CustomerProfileResponse> getAllCustomerList();
}