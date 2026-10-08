package com.loanapp.loan_application.dto.foreclosure;


import com.loanapp.loan_application.entity.foreclosure.ForeClosureStatus;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureType;
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
public class ForeClosureResponseDto {

    private Long foreClosureId;

    private Long loanAccountId;

    private ForeClosureType foreClosureType;

    private BigDecimal foreClosureAmount;

    private BigDecimal partialAmount;

    private ForeClosureStatus status;

    private Boolean isPaid;

    private LocalDateTime requestedAt;

    private LocalDateTime approvedAt;

    private Long closedBy;

    private LocalDateTime paymentDate;

    private String remarks;
}