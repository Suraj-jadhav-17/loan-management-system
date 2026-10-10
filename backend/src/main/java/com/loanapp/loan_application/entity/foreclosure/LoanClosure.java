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
@Table(name = "LoanClosures")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanClosure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LoanClosureId")
    private Long loanClosureId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LoanAccountId", nullable = false, unique = true)
    private LoanAccount loanAccount;

    @Enumerated(EnumType.STRING)
    @Column(name = "ClosureType", nullable = false)
    private LoanClosureType closureType;

    @Column(name = "FinalSettlementAmount", nullable = false, precision = 18, scale = 2)
    private BigDecimal finalSettlementAmount;

    @Column(name = "ClosureDate", nullable = false)
    private LocalDateTime closureDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "ClosureStatus", nullable = false)
    private LoanClosureStatus closureStatus;

    @Column(name = "ClosedBy")
    private Long closedBy;

    @Column(name = "NocGenerated")
    private Boolean nocGenerated;

    @Column(name = "CibilUpdated")
    private Boolean cibilUpdated;
}