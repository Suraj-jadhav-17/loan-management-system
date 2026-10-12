package com.loanapp.loan_application.dto.foreclosure;

import com.loanapp.loan_application.entity.foreclosure.LoanClosureStatus;
import com.loanapp.loan_application.entity.foreclosure.LoanClosureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanClosureResponseDto {

    private Long loanClosureId;

    private Long loanAccountId;

    private LoanClosureType closureType;

    private BigDecimal finalSettlementAmount;

    private LocalDateTime closureDate;

    private LoanClosureStatus closureStatus;

    private Long closedBy;

    private Boolean nocGenerated;

    private Boolean cibilUpdated;
}