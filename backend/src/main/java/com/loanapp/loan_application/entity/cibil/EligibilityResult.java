package com.loanapp.loan_application.entity.cibil;

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
    @Column(name = "eligibility_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    private Integer cibilScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "EligibilityStatus")
    private EligibilityStatus eligibilityStatus;

    @Column(precision = 18, scale = 2)
    private BigDecimal loanAmount;

    private String decision;


    private RiskCategory riskCategory;

    private String borrowingLimit;

    private String rejectionReason;
}