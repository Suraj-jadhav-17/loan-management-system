package com.loanapp.loan_application.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class CustomerResponse {

    private Long customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String employmentType;
    private BigDecimal monthlyIncome;
    private LocalDateTime createdAt;
}