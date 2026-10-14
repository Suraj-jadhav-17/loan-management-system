package com.loanapp.loan_application.dto.LoanOfficer;


import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerResponseList {


        private String firstName;
        private String lastName;
        private String email;
        private Long age;
        private String password;
        private String mobileNo;
        private String panCard;
        private String aadhaarNo;
        private String employmentType;
        private BigDecimal monthlyIncome;
        private BigDecimal monthlyInvestment;


    }
