package com.loanapp.loan_application.entity.penalty;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class PenaltyCharges {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long penaltyChargeId;

    @Column(nullable = false)
    private Long emiScheduleId;

    @Column(nullable = false)
    private Long loanAccountId;

    private BigDecimal penaltyAmount;

    private String reason;

    private String status;

    private LocalDateTime createdAt;
}