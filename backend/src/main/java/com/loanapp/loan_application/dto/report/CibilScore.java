package com.loanapp.loan_application.dto.report;

import lombok.Data;

@Data
public class CibilScore {
    private Long bibilScoreRange;
    private Long applicants;
    private Long approved;
    private Long approvedPerCentage;
}
