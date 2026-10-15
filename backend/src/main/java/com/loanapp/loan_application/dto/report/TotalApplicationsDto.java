package com.loanapp.loan_application.dto.report;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TotalApplicationsDto {

    private Long totalApplications;

    private Long activeApplications;

    private Long closedApplications;

    private Long pandingApplication;

}

