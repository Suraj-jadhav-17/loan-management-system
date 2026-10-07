package com.loanapp.loan_application.entity.emischeduler;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Data

public class EmiSchedules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long emiScheduleId;

    @Column(nullable=false)
    private Long loanAccountId;
    @Column(nullable = false)
    private Long installmentNo;
    @Column(nullable = false)
    private LocalDate dueDate;
    private BigDecimal principalAmount;
    private BigDecimal interestAmount;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private BigDecimal emi;
    private String paymentStatus;
    private LocalDateTime paidDate;
    private String cancellationReason;
}

