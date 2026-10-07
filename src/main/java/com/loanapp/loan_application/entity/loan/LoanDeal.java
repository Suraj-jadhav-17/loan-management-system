package com.loanapp.loan_application.entity.loan;

import com.loanapp.loan_application.entity.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private  Double amount;

    private Double interestRate;

   private Integer tenureMonths;

   private Double emiAmount;

   private String bankName;
   private String bankAccountNumber;
   private String ifscCode;
   private Integer emiDay;
   private Double approvedAmount;



}
