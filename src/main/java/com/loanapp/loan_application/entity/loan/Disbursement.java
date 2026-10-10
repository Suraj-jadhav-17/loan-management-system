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
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Disbursements")
public class Disbursement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long disbursementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DealId", nullable = false)
    private LoanDeal loanDeal;  //LoanDeal

    @Column(name = "DisburseAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal disburseAmount;

    @Column(name = "BankPartner", length = 200)
    private String bankPartner;

    @Column(name = "DisbursementDate")
    private LocalDateTime disbursementDate;

    @Column(name = "Status", length = 50)
    private DisbursementStatus status;
}