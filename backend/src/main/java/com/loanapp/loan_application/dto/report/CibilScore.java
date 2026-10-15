package com.loanapp.loan_application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CibilScore {
    private String bibilScoreRange;
    private Long applicants;
    private Long approved;
    private Double approvedPerCentage;


}
