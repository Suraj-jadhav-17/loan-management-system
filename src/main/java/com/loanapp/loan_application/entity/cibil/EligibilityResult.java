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
@Table(name = "EligibilityResults")
public class EligibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EligibilityId")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CustomerId", nullable = false)
    private Customer customer;

    @Column(name = "CibilScore")
    private Integer cibilScore;

    @Column(name = "IsEligible")
    private Boolean isEligible;

    @Column(name = "LoanAmount", precision = 18, scale = 2)
    private BigDecimal loanAmount;

    @Column(name = "Decision")
    private String decision;

    @Column(name = "RiskCategory")
    private String riskCategory;

    @Column(name = "BorrowingLimit")
    private String borrowingLimit;

    @Column(name = "RejectionReason")
    private String rejectionReason;
}
