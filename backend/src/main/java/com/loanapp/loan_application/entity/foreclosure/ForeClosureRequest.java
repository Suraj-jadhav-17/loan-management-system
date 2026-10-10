package com.loanapp.loan_application.entity.foreclosure;


import com.loanapp.loan_application.entity.loanaccount.LoanAccount;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ForeClosureRequests")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForeClosureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ForeClosureId")
    private Long foreClosureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LoanAccountId", nullable = false)
    private LoanAccount loanAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "ForeClosureType", nullable = false)
    private ForeClosureType foreClosureType;

    @Column(name = "ForeClosureAmount", precision = 18, scale = 2)
    private BigDecimal foreClosureAmount;

    @Column(name = "PartialAmount", precision = 18, scale = 2)
    private BigDecimal partialAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status", nullable = false)
    private ForeClosureStatus status;

    @Column(name = "IsPaid", nullable = false)
    private Boolean isPaid;

    @Column(name = "RequestedAt", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "ApprovedAt")
    private LocalDateTime approvedAt;

    @Column(name = "ClosedBy")
    private Long closedBy;

    @Column(name = "PaymentDate")
    private LocalDateTime paymentDate;

    @Column(name = "Remarks")
    private String remarks;
}