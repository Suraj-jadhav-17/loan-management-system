package com.loanapp.loan_application.entity.registration;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Customers")
@Data
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CustomerId")
    private Long customerId;

    @Column(name = "FirstName", nullable = false)
    private String firstName;

    @Column(name = "LastName")
    private String lastName;

    @Column(name = "Email", nullable = false, unique = true)
    private String email;

    @Column(name = "MobileNo")
    private String mobileNo;

    @Column(name = "Password")
    private String password;

    @Column(name = "AadhaarNo")
    private String aadhaarNo;

    @Column(name = "EmploymentType")
    private String employmentType;

    @Column(name = "MonthlyIncome", precision = 18, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "MonthlyInvestment", precision = 18, scale = 2)
    private BigDecimal monthlyInvestment;

    @Column(name = "IsEmailVerified", nullable = false)
    private Boolean isEmailVerified = false;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }
}