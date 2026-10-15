package com.loanapp.loan_application.serviceimpl.report;

import com.loanapp.loan_application.dto.report.CibilScore;
import com.loanapp.loan_application.dto.report.TotalApplicationsDto;
import com.loanapp.loan_application.dto.report.TotalCibil;
import com.loanapp.loan_application.entity.cibil.EligibilityStatus;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.repository.cibil.EligibilityResultRepo;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.service.report.ReportService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    private final LoanDealRepo loanDealRepo;

    private final EligibilityResultRepo eligibilityResultRepo;

    public ReportServiceImpl(LoanDealRepo loanDealRepo, EligibilityResultRepo eligibilityResultRepo) {
        this.loanDealRepo = loanDealRepo;
        this.eligibilityResultRepo = eligibilityResultRepo;

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

    @Override
    public Map<String, Object>getCibilScore() {

        long range750To900 = eligibilityResultRepo.countByCibilScoreBetween(750, 900);
        long range650To749 = eligibilityResultRepo.countByCibilScoreBetween(650, 749);
        long range550To649 = eligibilityResultRepo.countByCibilScoreBetween(550, 649);
        long range300To549 = eligibilityResultRepo.countByCibilScoreBetween(300, 549);

        long eligible750To900 = eligibilityResultRepo.countByCibilScoreBetweenAndEligibilityStatus(750, 900, EligibilityStatus.ELIGIBLE);
        long eligible650To749 = eligibilityResultRepo.countByCibilScoreBetweenAndEligibilityStatus(650, 749, EligibilityStatus.ELIGIBLE);
        long eligible550To649 = eligibilityResultRepo.countByCibilScoreBetweenAndEligibilityStatus(550, 649, EligibilityStatus.ELIGIBLE);
        long eligible300To549 = eligibilityResultRepo.countByCibilScoreBetweenAndEligibilityStatus(300, 549, EligibilityStatus.ELIGIBLE);

        double approvedPerCentage750To900 = range750To900 == 0 ? 0.0 : (eligible750To900 * 100.0) / range750To900;
        double approvedPerCentage650To749 = range650To749 == 0 ? 0.0 : (eligible650To749 * 100.0) / range650To749;
        double approvedPerCentage550To649 = range550To649 == 0 ? 0.0 : (eligible550To649 * 100.0) / range550To649;
        double approvedPerCentage300To549 = range300To549 == 0 ? 0.0 : (eligible300To549 * 100.0) / range300To549;

        long applicants = eligibilityResultRepo.count();
        long approved = eligibilityResultRepo.countByEligibilityStatus(EligibilityStatus.ELIGIBLE);

        double approval = applicants == 0 ? 0.0 : (approved * 100.0) / applicants;

        List<CibilScore> result = new ArrayList<>();

        result.add(new CibilScore("750 - 900", range750To900, eligible750To900, approvedPerCentage750To900));
        result.add(new CibilScore("650 - 749", range650To749, eligible650To749, approvedPerCentage650To749));
        result.add(new CibilScore("550 - 649", range550To649, eligible550To649, approvedPerCentage550To649));
        result.add(new CibilScore("300 - 549", range300To549, eligible300To549, approvedPerCentage300To549));


        TotalCibil totalCibil = new TotalCibil();
        totalCibil.setTotal("100-1000");
        totalCibil.setTotalApplicants(applicants);
        totalCibil.setTotalApprove(approved);
        totalCibil.setTotalApproval(approval);

        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("totalCibil", totalCibil);

        return response;
    }
}
