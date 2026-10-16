package com.loanapp.loan_application.serviceimpl.report;

import com.loanapp.loan_application.dto.report.*;
import com.loanapp.loan_application.entity.cibil.EligibilityStatus;
import com.loanapp.loan_application.entity.cibil.RiskCategory;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.cibil.ScoreCardStatus;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.repository.cibil.EligibilityResultRepo;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
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
    private final ScoreCardRepo scoreCardRepo;
    private final EligibilityResultRepo eligibilityResultRepo;

    public ReportServiceImpl(LoanDealRepo loanDealRepo, ScoreCardRepo scoreCardRepo, EligibilityResultRepo eligibilityResultRepo) {
        this.loanDealRepo = loanDealRepo;
        this.scoreCardRepo = scoreCardRepo;
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
    public Map<String, Object> getCibilScore() {

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

    @Override
    public Map<String, Object> scoreCardReport() {

        long high = scoreCardRepo.countByRiskCategory(RiskCategory.HIGH);
        long low = scoreCardRepo.countByRiskCategory(RiskCategory.LOW);
        long medium = scoreCardRepo.countByRiskCategory(RiskCategory.MEDIUM);

        long highApproveEligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.HIGH, ScoreCardStatus.ELIGIBLE);

        long highApproveIneligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.HIGH, ScoreCardStatus.REJECTED);

        long highApproveUnderReview = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.HIGH, ScoreCardStatus.PENDING);

        long lowApproveEligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.LOW, ScoreCardStatus.ELIGIBLE);

        long lowApproveIneligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.LOW, ScoreCardStatus.REJECTED);

        long lowApproveUnderReview = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.LOW, ScoreCardStatus.PENDING);

        long mediumApproveEligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.MEDIUM, ScoreCardStatus.ELIGIBLE);

        long mediumApproveIneligible = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.MEDIUM, ScoreCardStatus.REJECTED);

        long mediumApproveUnderReview = scoreCardRepo.countByRiskCategoryAndCurrentStatus(RiskCategory.MEDIUM, ScoreCardStatus.PENDING);

        ScoreCard highApproveDate = scoreCardRepo.findByRiskCategory(RiskCategory.HIGH).orElse(null);

        ScoreCard lowApproveDate = scoreCardRepo.findByRiskCategory(RiskCategory.LOW).orElse(null);

        ScoreCard mediumApproveDate = scoreCardRepo.findByRiskCategory(RiskCategory.MEDIUM).orElse(null);

        String highDate = highApproveDate != null ? highApproveDate.getAppliedDate().toString() : null;

        String lowDate = lowApproveDate != null ? lowApproveDate.getAppliedDate().toString() : null;

        String mediumDate = mediumApproveDate != null ? mediumApproveDate.getAppliedDate().toString() : null;


        long highApprovalPercentage = high == 0 ? 0 : (highApproveEligible * 100) / high;

        long lowApprovalPercentage = low == 0 ? 0 : (lowApproveEligible * 100) / low;

        long mediumApprovalPercentage = medium == 0 ? 0 : (mediumApproveEligible * 100) / medium;


        List<ScoreCardWithReport> result = new ArrayList<>();

        result.add(new ScoreCardWithReport("high", high, highApproveEligible, highApproveIneligible, highApproveUnderReview, highApprovalPercentage, highDate));

        result.add(new ScoreCardWithReport("medium", medium, mediumApproveEligible, mediumApproveIneligible, mediumApproveUnderReview, mediumApprovalPercentage,mediumDate));

        result.add(new ScoreCardWithReport("low", low, lowApproveEligible, lowApproveIneligible, lowApproveUnderReview, lowApprovalPercentage, lowDate));

        TotalScoreCardReport totalScoreCardReport = new TotalScoreCardReport();

        long totalApplicants = scoreCardRepo.count();

        long totalApprove = scoreCardRepo.countByCurrentStatus(ScoreCardStatus.ELIGIBLE);

        long totalRejected = scoreCardRepo.countByCurrentStatus(ScoreCardStatus.REJECTED);

        long totalUnderReview = scoreCardRepo.countByCurrentStatus(ScoreCardStatus.PENDING);

        long totalApprovalPercentage = totalApplicants == 0 ? 0 : (totalApprove * 100) / totalApplicants;

        totalScoreCardReport.setTotal("Total");
        totalScoreCardReport.setTotalApplicants(totalApplicants);
        totalScoreCardReport.setTotalApprove(totalApprove);
        totalScoreCardReport.setTotalRejected(totalRejected);
        totalScoreCardReport.setTotalPanding(totalUnderReview);
        totalScoreCardReport.setTotalApprovePercentage(totalApprovalPercentage);

        Map<String, Object> response = new HashMap<>();

        response.put("result", result);
        response.put("totalScoreCardReport", totalScoreCardReport);

        return response;
    }
}
