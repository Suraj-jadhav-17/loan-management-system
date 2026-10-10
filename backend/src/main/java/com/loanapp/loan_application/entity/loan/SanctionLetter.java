package com.loanapp.loan_application.entity.loan;

import jakarta.persistence.*;
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
public class SanctionLetter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String applicationNo;


    @OneToOne(cascade = CascadeType.ALL)
    private  LoanDeal loanDeal;

    private BigDecimal loanAmount;

    private BigDecimal interestRate;

    private Long tenureMonth;

    private BigDecimal emiAmount;

    private LocalDateTime createdAt;

}
