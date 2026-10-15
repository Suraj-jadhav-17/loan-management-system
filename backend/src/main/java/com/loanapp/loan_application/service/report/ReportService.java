package com.loanapp.loan_application.service.report;

import com.loanapp.loan_application.dto.report.CibilScore;
import com.loanapp.loan_application.dto.report.TotalApplicationsDto;

import java.util.List;
import java.util.Map;

public interface ReportService {
    TotalApplicationsDto getAllApplication();

    Map<String, Object> getCibilScore();

}
