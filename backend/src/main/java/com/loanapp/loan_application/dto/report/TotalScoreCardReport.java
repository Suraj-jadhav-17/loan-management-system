package com.loanapp.loan_application.dto.report;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalScoreCardReport {
    private String total;
    private Long totalApplicants;
    private Long totalApprove;
    private Long totalRejected;
    private Long totalPanding;
    private Long totalApprovePercentage;
}
