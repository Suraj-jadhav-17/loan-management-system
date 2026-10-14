package com.loanapp.loan_application.dto.dashBoard;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CustomerDashboardResponse {

    private long activeLoan;

    private BigDecimal totalPaidAmount;

    private BigDecimal upcomingEmi;

    private LocalDate upcomingEmiDate;

    private long paidEmi;

    private long totalApplication;
}