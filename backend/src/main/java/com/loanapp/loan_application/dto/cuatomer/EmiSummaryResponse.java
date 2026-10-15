package com.loanapp.loan_application.dto.cuatomer;


import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmiSummaryResponse {

    private BigDecimal totalPaidAmount;
    private long totalPaidEmi;

    private BigDecimal upcomingEmiAmount;
    private Long upcomingEmiNumber;
    private LocalDate upcomingEmiDate;

    private BigDecimal remainingEmiAmount;
    private long remainingEmiNumber;
}

