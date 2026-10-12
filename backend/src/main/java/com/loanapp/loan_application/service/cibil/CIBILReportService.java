package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CIBILReportService {

    CIBILReportResponseDto createReport(CIBILReportRequestDto request);

    CIBILReportResponseDto getLatestReport(Long customerId);

    Page<CIBILReportResponseDto> getReportHistory(Long customerId, Pageable pageable);
}
