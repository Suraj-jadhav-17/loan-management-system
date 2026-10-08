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
@Table(name = "CibilReports")
public class CIBILReport {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   @Column(name = "CibilReportId")
   private Long id;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "CustomerId", nullable = false)
   private Customer customer;

   @Column(name = "PanNo")
   private String panNo;

   @Column(name = "CibilScore", nullable = false)
   private Integer cibilScore;

   @Column(name = "CheckDate", nullable = false)
   private LocalDateTime checkDate;
}
