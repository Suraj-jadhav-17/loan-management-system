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

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    private Customer customer;


    private Integer cibilScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "EligibilityStatus")
    private String eligibilityStatus;

    @Column( precision = 18, scale = 2)
    private BigDecimal loanAmount;


    private String decision;


    private String riskCategory;


    private String borrowingLimit;


    private String rejectionReason;
}