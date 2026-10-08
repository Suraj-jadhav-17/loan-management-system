package com.loanapp.loan_application.entity.loan;

import com.loanapp.loan_application.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    private LoanType loanType;

    private BigDecimal amount;

    private BigDecimal interestRate;

   private Long tenureMonths;

   private BigDecimal emiAmount;

   private String bankName;
   private String bankAccountNumber;
   private String ifscCode;
   private Long emiDay;
   private BigDecimal approvedAmount;



}
