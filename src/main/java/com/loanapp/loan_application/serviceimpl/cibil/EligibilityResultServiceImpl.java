package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.cibil.EligibilityResult;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.cibil.EligibilityResultRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.EligibilityResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class EligibilityResultServiceImpl implements EligibilityResultService {

    private final EligibilityResultRepo eligibilityResultRepo;
    private final CIBILReportRepo cibilReportRepo;
    private final CustomerRepository customerRepository;

    @Override
    @CacheEvict(value = "latestEligibility", key = "#request.customerId")
    public EligibilityResultResponseDto checkEligibility(EligibilityResultRequestDto request) {

        log.info("Checking eligibility for customerId={}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        CIBILReport report = cibilReportRepo.findTopByCustomerCustomerIdOrderByCheckDateDesc(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("CIBIL report not found for customer: " + request.getCustomerId()));

        Integer score = report.getCibilScore();

        Decision decision = getDecision(score);

        EligibilityResult result = EligibilityResult.builder()
                .customer(customer)
                .cibilScore(score)
                .isEligible(decision.isEligible())
                .decision(decision.decision())
                .riskCategory(decision.riskCategory())
                .borrowingLimit(decision.borrowingLimit())
                .loanAmount(decision.loanAmount())
                .rejectionReason(decision.rejectionReason())
                .build();

        return mapToResponse(eligibilityResultRepo.save(result));
    }

    @Override
    @Cacheable(value = "latestEligibility", key = "#customerId")
    public EligibilityResultResponseDto getLatestEligibility(Long customerId) {

        EligibilityResult result = eligibilityResultRepo.findTopByCustomerCustomerIdOrderByIdDesc(customerId)
                .orElseThrow(() -> new RuntimeException("Eligibility result not found for customer: " + customerId));

        return mapToResponse(result);
    }

    @Override
    public Page<EligibilityResultResponseDto> getEligibilityHistory(Long customerId, Pageable pageable) {

        return eligibilityResultRepo.findByCustomerCustomerIdOrderByIdDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    private Decision getDecision(Integer score) {

        if (score == null || score < 300 || score > 900) {
            throw new RuntimeException("CIBIL score must be between 300 and 900");
        }

        if (score >= 900) {
            return new Decision(true, "AUTO_APPROVE", "Excellent", "Above 1 Crore", null, null);
        }

        if (score >= 800) {
            return new Decision(true, "AUTO_APPROVE", "Very Good", "75 Lakh to 1 Crore", BigDecimal.valueOf(10_000_000), null);
        }

        if (score >= 750) {
            return new Decision(null, "MANUAL_REVIEW", "Good", "50 Lakh to 75 Lakh", BigDecimal.valueOf(7_500_000), "Manual officer review required.");
        }

        if (score >= 700) {
            return new Decision(null, "MANUAL_REVIEW", "Average", "25 Lakh to 50 Lakh", BigDecimal.valueOf(5_000_000), "Manual officer review required.");
        }

        if (score >= 650) {
            return new Decision(null, "MANUAL_REVIEW", "Risky", "10 Lakh to 25 Lakh", BigDecimal.valueOf(2_500_000), "Manual officer review required.");
        }

        return new Decision(false, "AUTO_REJECT", "Reject", "Less than ₹10 Lakh", BigDecimal.ZERO, "CIBIL score is below 650.");
    }

    private EligibilityResultResponseDto mapToResponse(EligibilityResult result) {
        return EligibilityResultResponseDto.builder()
                .id(result.getId())
                .customerId(result.getCustomer().getCustomerId())
                .cibilScore(result.getCibilScore())
                .isEligible(result.getIsEligible())
                .decision(result.getDecision())
                .riskCategory(result.getRiskCategory())
                .borrowingLimit(result.getBorrowingLimit())
                .loanAmount(result.getLoanAmount())
                .rejectionReason(result.getRejectionReason())
                .build();
    }

    private record Decision(
            Boolean isEligible,
            String decision,
            String riskCategory,
            String borrowingLimit,
            BigDecimal loanAmount,
            String rejectionReason) {
    }
}
