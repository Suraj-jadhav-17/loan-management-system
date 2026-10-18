
        package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.cibil.EligibilityStatus;
import com.loanapp.loan_application.entity.cibil.RiskCategory;
import com.loanapp.loan_application.entity.register.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "eligibility_results")
public class EligibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_customer_id", nullable = false)
    private Customer customer;

    @Column(name = "cibil_score")
    private Integer cibilScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "eligibility_status")
    private EligibilityStatus eligibilityStatus;

    @Column(name = "loan_amount", precision = 18, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "decision")
    private String decision;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_category")
    private RiskCategory riskCategory;

    @Column(name = "borrowing_limit")
    private String borrowingLimit;

    @Column(name = "rejection_reason")
    private String rejectionReason;
}