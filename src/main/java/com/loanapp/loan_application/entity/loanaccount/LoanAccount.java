package com.loanapp.loan_application.entity.loanaccount;

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
@Table(name = "LoanAccounts")
public class LoanAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long loanAccountId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Long customer;  //Customer

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DealId", nullable = false)
    private Long loanDeal;  //LoanDeal

    @Column(name = "LoanAccountNo", nullable = false, unique = true, length = 100)
    private String loanAccountNo;

    @Column(name = "LoanAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "OutstandingPrincipal", precision = 18, scale = 2)
    private BigDecimal outstandingPrincipal;

    @Column(name = "LoanStatus", length = 50)
    private String loanStatus;

    @Column(name = "InterestRate", precision = 8, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "TenureMonths")
    private Integer tenureMonths;

    @Column(name = "EmiAmount", precision = 18, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "DisbursementDate")
    private LocalDateTime disbursementDate;

    @Column(name = "TotalPaidAmount", precision = 18, scale = 2)
    private BigDecimal totalPaidAmount;

    @Column(name = "CreatedAt", nullable = false)
    private LocalDateTime createdAt;
}