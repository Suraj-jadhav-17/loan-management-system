package com.loanapp.loan_application.dto.register;

import jakarta.persistence.Column;
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

    @NotNull(message = "Age is required")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 60, message = "Age must not exceed 100")
    private Long age;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;
    @NotBlank
    @Size(min = 10, max = 10, message = "Mobile number must be exactly 10 digits")
    private String mobileNo;

    @NotBlank(message = "PAN number is required")
    @Pattern(
            regexp = "^[A-Z]{5}[0-9]{4}[A-Z]$",
            message = "Invalid PAN number"
    )
    private String panCard;

    @NotBlank
    @Size(min = 12, max = 12, message = "Aadhaar number must be exactly 12 digits")
    private String aadhaarNo;

    private String employmentType;

    @NotNull
    @Positive
    private BigDecimal monthlyIncome;

    @Column(name = "MonthlyInvestment", precision = 18, scale = 2)
    private BigDecimal monthlyInvestment;

    private String otp;


}