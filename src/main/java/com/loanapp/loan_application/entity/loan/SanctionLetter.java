package com.loanapp.loan_application.entity.loan;

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
@Table(name = "SanctionLetters")
public class SanctionLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sanctionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DealId", nullable = false)
    private Long loanDeal;  //LoanDeal

    @Column(name = "LoanAmount", precision = 18, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "InterestRate", precision = 8, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "TenureMonths")
    private Integer tenureMonths;

    @Column(name = "EmiAmount", precision = 18, scale = 2)
    private BigDecimal emiAmount;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}