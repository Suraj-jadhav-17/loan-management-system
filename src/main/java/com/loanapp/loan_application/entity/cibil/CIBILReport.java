package com.loanapp.loan_application.entity.cibil;

import com.loanapp.loan_application.entity.register.Customer;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CIBILReport {
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @OneToOne(fetch = FetchType.LAZY)
   private Customer customer;

   private String panNo;

   private Integer cibilScore;


   private LocalDateTime checkDate;



}
