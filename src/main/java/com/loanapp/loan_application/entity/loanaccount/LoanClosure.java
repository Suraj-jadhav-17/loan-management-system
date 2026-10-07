package com.loanapp.loan_application.entity.loanaccount;

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
@Table(name = "LoanClosures")
public class LoanClosure {




    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long closureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LoanAccountId", nullable = false)
    private LoanAccount loanAccount;

    @Column(name = "ClosureType", length = 50)
    private String closureType;

    @Column(name = "FinalSettlementAmount", precision = 18, scale = 2)
    private BigDecimal finalSettlementAmount;

    @Column(name = "ClosureDate")
    private LocalDateTime closureDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClosedBy")
    private Long closedBy;  //User

    @Column(name = "Remarks", length = 1000)
    private String remarks;

    @Column(name = "ClosureStatus", length = 50)
    private String closureStatus;
}