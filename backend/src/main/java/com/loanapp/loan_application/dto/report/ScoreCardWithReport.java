package com.loanapp.loan_application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCardWithReport {

    private String  scoreCard;
    private Long applicants;
    private Long approve;
    private Long rejected;
    private Long panding;
    private Long approvalPerCentage;
    private String approveDate;



}
