package com.loanapp.loan_application.entity.loan;


import com.loanapp.loan_application.entity.register.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;  //Customer

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DealId", nullable = false)
    private LoanDeal loanDeal;  //LoanDeal

    @Column( nullable = false, unique = true, length = 100)
    private String loanAccountNo;


    private BigDecimal loanAmount;


    private BigDecimal outstandingPrincipal;

    @Enumerated(EnumType.STRING)
    private LoanStatus loanStatus;


    private BigDecimal interestRate;


    private Long tenureMonths;


    private BigDecimal emiAmount;


    private LocalDateTime disbursementDate;


    private BigDecimal totalPaidAmount;


    private LocalDateTime createdAt;
}