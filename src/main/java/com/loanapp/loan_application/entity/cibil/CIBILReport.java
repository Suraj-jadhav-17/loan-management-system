package com.loanapp.loan_application.entity.cibil;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

   private Customer customer;

   private String panNo;

   private Integer cibilScore;

   private LocalDateTime checkDate;



}
