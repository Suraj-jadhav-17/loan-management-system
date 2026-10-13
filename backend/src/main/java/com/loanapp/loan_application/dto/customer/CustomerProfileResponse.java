package com.loanapp.loan_application.dto.customer;

import jakarta.persistence.Column;
import lombok.Data;

@Data
public class CustomerProfileResponse {


    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String employmentType;
    private String panCard;
    private Long age;


}