package com.loanapp.loan_application.entity.loanaccount;

import com.loanapp.loan_application.entity.loan.LoanAccount;
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
@Table(name = "ForeClosureRequests")
public class ForeClosureRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long requestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LoanAccountId", nullable = false)
    private LoanAccount loanAccount;

    @Column(name = "ForeClosureType", length = 50)
    private String foreClosureType;

    @Column(name = "ForeClosureAmount", precision = 18, scale = 2)
    private BigDecimal foreClosureAmount;

    @Column(name = "PartialAmount", precision = 18, scale = 2)
    private BigDecimal partialAmount;

    @Column(name = "RequestedDate")
    private LocalDateTime requestedDate;

    @Column(name = "ExpectedClosureDate")
    private LocalDateTime expectedClosureDate;

    @Column(name = "Reason", length = 1000)
    private String reason;

    @Column(name = "Status", length = 50)
    private String status;

    @Column(name = "ApprovedDate")
    private LocalDateTime approvedDate;

    @Column(name = "IsPaid", nullable = false)
    private Boolean isPaid;

    @Column(name = "PaidDate")
    private LocalDateTime paidDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ClosedBy")
    private Long closedBy;  //User
}