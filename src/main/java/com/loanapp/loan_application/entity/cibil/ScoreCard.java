package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.register.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "ScoreCards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ScoreCardId")
    private Long scoreCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;

    @Column(name = "CibilScore")
    private Integer cibilScore;

    @Column(name = "Foir", precision = 8, scale = 2)
    private BigDecimal foir;

    @Column(name = "IncomeScore")
    private Integer incomeScore;

    @Column(name = "EmploymentScore")
    private Integer employmentScore;

    @Column(name = "AgeScore")
    private Integer ageScore;

    @Column(name = "FoirScore")
    private Integer foirScore;

    @Column(name = "TotalScore")
    private Integer totalScore;

    @Column(name = "EligibleLoanAmount", precision = 18, scale = 2)
    private BigDecimal eligibleLoanAmount;
}