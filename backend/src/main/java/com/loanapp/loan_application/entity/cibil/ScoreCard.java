package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.cibil.ScoreCardStatus;
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
    private Long scoreCardId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(length = 100)
    private ScoreCardStatus currentStatus;

    @Column(length = 1000)
    private String rejectionReason;

    private LocalDateTime appliedDate;
    private Integer cibilScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private RiskCategory riskCategory;

    @Column(precision = 18, scale = 2)
    private BigDecimal eligibleLoanAmount;

    @Column(precision = 8, scale = 2)
    private BigDecimal foir;

    private Integer totalScore;


}