package com.loanapp.loan_application.serviceimpl.report;

import com.loanapp.loan_application.dto.report.TotalApplicationsDto;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.service.report.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

 private final LoanDealRepo loanDealRepo;

    public ReportServiceImpl(LoanDealRepo loanDealRepo) {
        this.loanDealRepo = loanDealRepo;
    }

    @Override
    public TotalApplicationsDto getAllApplication() {

        long total = loanDealRepo.count();
        long active = loanDealRepo.countByStatus(LoanStatus.ACTIVE);

        long closed = loanDealRepo.countByStatus(LoanStatus.CLOSED);
        long pandding = loanDealRepo.countByStatus(LoanStatus.PANNDING);

        TotalApplicationsDto totalApplicationsDto = new TotalApplicationsDto();
        totalApplicationsDto.setActiveApplications(active);
        totalApplicationsDto.setPandingApplication(pandding);
        totalApplicationsDto.setClosedApplications(closed);
        totalApplicationsDto.setTotalApplications(total);

        return totalApplicationsDto;
    }
}
