package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;

public interface CIBILReportService {

    CIBILReportResponseDto calculateAndCreateReport(CIBILReportRequestDto request);

    CIBILReportResponseDto getLatestReport(Long customerId);
}