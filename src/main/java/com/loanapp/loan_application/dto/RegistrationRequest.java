package com.loanapp.loan_application.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegistrationRequest {

    @NotBlank
    private String firstName;

    private String lastName;

    @NotBlank
    @Email
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;
    @NotBlank
    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    private String mobileNo;

    @NotBlank
    @Size(min = 12, max = 12, message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNo;

    private String employmentType;

    @NotNull
    @Positive
    private BigDecimal monthlyIncome;
}