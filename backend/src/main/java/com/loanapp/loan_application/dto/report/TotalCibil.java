package com.loanapp.loan_application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TotalCibil {
    private String total;
    private Long TotalApplicants;
    private Long TotalApprove;
    private  Double TotalApproval;
    private Date approveDate;
}
