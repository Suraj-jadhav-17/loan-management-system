package com.loanapp.loan_application.entity.scorecard;


import com.loanapp.loan_application.entity.Customer;
import com.loanapp.loan_application.entity.cibil.CIBILReport;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;

    private Customer customer;

    private ScoreCardStatus status;

    private String rejectionReason;

    private LocalDateTime appliedDate;

    private CIBILReport cibilScore ;

    private RiskCategory riskCategory;

    private BigDecimal eligibleLoanAmount;


}
