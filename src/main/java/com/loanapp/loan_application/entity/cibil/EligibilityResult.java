package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.Customer;
import jakarta.persistence.*;
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
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private Integer cibilScore;

    private Boolean isEligible;

    private Double loanAmount;

    private String rejectionReason;


}
