package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.register.Customer;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EligibilityResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
<<<<<<< HEAD
    private Long id;

    private Customer customer;

    private Integer cibilScore;

    private Boolean isEligible;

    private Double loanAmount;

=======

    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn( nullable = false)
    private Customer customer;


    private Integer cibilScore;


    private String eligibilityStatus;

    @Column( precision = 18, scale = 2)
    private BigDecimal loanAmount;


    private String decision;


    private String riskCategory;


    private String borrowingLimit;


>>>>>>> a742ca952b7b787ac1a7446b01b9165cfe2c7c56
    private String rejectionReason;


}
