package com.loanapp.loan_application.entity.loan;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "LoanAccounts")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanAccountId;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;


    private BigDecimal emiAmount;

    private LocalDateTime disbursementDate;

}