package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @Column(name = "CurrentStatus")
    private String currentStatus;

    @Column(name = "RejectionReason")
    private String rejectionReason;

    @Column(name = "AppliedDate")
    private LocalDateTime appliedDate;

    @Column(name = "CibilScore")
    private Integer cibilScore;

    @Column(name = "RiskCategory")
    private String riskCategory;

    @Column(name = "EligibleLoanAmount", precision = 18, scale = 2)
    private BigDecimal eligibleLoanAmount;
}