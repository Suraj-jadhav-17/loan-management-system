
package com.loanapp.loan_application.entity.foreclosure;

import com.loanapp.loan_application.entity.loan.LoanAccount;
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
    private Long foreClosureId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private LoanAccount loanAccount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForeClosureType foreClosureType;

    @Column(precision = 18, scale = 2)
    private BigDecimal foreClosureAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal partialAmount;

    @Column(precision = 18, scale = 2)
    private BigDecimal outstandingPrincipal;

    @Column(precision = 18, scale = 2)
    private BigDecimal accruedInterest;

    @Column(precision = 18, scale = 2)
    private BigDecimal overdueDues;

    @Column(precision = 18, scale = 2)
    private BigDecimal foreclosureFee;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ForeClosureStatus status;

    @Column(nullable = false)
    private Boolean isPaid;

    @Column(nullable = false)
    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;
    private Long closedBy;
    private LocalDateTime paymentDate;
    private String remarks;
}
