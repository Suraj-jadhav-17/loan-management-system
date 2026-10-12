
package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.entity.cibil.EligibilityResult;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.repository.cibil.EligibilityResultRepo;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.EligibilityResultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EligibilityResultServiceImpl implements EligibilityResultService {

    private final EligibilityResultRepo eligibilityResultRepo;
    private final ScoreCardRepo scoreCardRepo;
    private final CustomerRepository customerRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final ModelMapper modelMapper;

    private static final List<String> REQUIRED_KYC_DOCUMENT_TYPES =
            List.of("Aadhaar Card", "PAN Card", "Salary Slip");

    @Override
    @CacheEvict(value = "latestEligibility", key = "#request.customerId")
    public EligibilityResultResponseDto checkEligibility(EligibilityResultRequestDto request) {

        log.info("Checking eligibility for customerId={}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        validateKycApproved(request.getCustomerId());

        ScoreCard scoreCard = scoreCardRepo.findTopByCustomerCustomerIdOrderByScoreCardIdDesc(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Scorecard not found for customer: " + request.getCustomerId()
                                                + ". Generate a scorecard first."));

        Integer score = scoreCard.getCibilScore();
        Decision decision = getDecision(score);

        EligibilityResult result = EligibilityResult.builder()
                .customer(customer)
                .cibilScore(score)
                .eligibilityStatus(decision.eligibilityStatus())
                .decision(decision.decision())
                .riskCategory(decision.riskCategory())
                .borrowingLimit(decision.borrowingLimit())
                .loanAmount(decision.loanAmount())
                .rejectionReason(decision.rejectionReason())
                .build();

        EligibilityResult savedResult = eligibilityResultRepo.save(result);
        EligibilityResultResponseDto response = modelMapper.map(savedResult, EligibilityResultResponseDto.class);

        response.setCustomerId(savedResult.getCustomer().getCustomerId());
        return response;
    }

    @Override
    @Cacheable(value = "latestEligibility", key = "#customerId")
    public EligibilityResultResponseDto getLatestEligibility(Long customerId) {

        EligibilityResult result = eligibilityResultRepo.findTopByCustomerCustomerIdOrderByIdDesc(customerId)
                        .orElseThrow(() -> new RuntimeException("Eligibility result not found for customer: " + customerId));

        EligibilityResultResponseDto response = modelMapper.map(result, EligibilityResultResponseDto.class);

        response.setCustomerId(result.getCustomer().getCustomerId());
        return response;
    }

    @Override
    public Page<EligibilityResultResponseDto> getEligibilityHistory(Long customerId, Pageable pageable) {

        return eligibilityResultRepo.findByCustomerCustomerIdOrderByIdDesc(customerId, pageable)
                .map(result -> {
                    EligibilityResultResponseDto response = modelMapper.map(result, EligibilityResultResponseDto.class);

                    response.setCustomerId(result.getCustomer().getCustomerId());
                    return response;
                });
    }

    private Decision getDecision(Integer score) {

        if (score == null || score < 0 || score > 1000) {throw new RuntimeException("Calculated score must be between 0 and 1000");
        }

        if (score >= 850) {
            return new Decision("ELIGIBLE", "AUTO_APPROVE", "LOW", "Above 75 Lakh",
                    BigDecimal.valueOf(10_000_000), null);
        }

        if (score >= 750) {
            return new Decision("ELIGIBLE", "MANUAL_REVIEW", "LOW", "50 Lakh to 75 Lakh",
                    BigDecimal.valueOf(7_500_000), "Manual officer review required.");
        }

        if (score >= 650) {
            return new Decision("UNDER_REVIEW", "MANUAL_REVIEW", "MEDIUM",
                    "25 Lakh to 50 Lakh", BigDecimal.valueOf(5_000_000), "Manual officer review required.");
        }

        if (score >= 550) {
            return new Decision("UNDER_REVIEW", "MANUAL_REVIEW", "MEDIUM", "10 Lakh to 25 Lakh",
                    BigDecimal.valueOf(2_500_000), "Manual officer review required.");
        }

        return new Decision("INELIGIBLE", "AUTO_REJECT", "HIGH", "Less than 10 Lakh",
                BigDecimal.ZERO, "Calculated score is below 550.");
    }

    private void validateKycApproved(Long customerId) {

        List<KycDocument> documents = kycDocumentRepository.findByCustomerIdOrderByDocumentIdDesc(
                                Math.toIntExact(customerId));

        Map<String, KycDocument> latestDocuments = new HashMap<>();

        for (KycDocument document : documents) {
            latestDocuments.putIfAbsent(document.getDocumentType(), document);
        }

        boolean approved = REQUIRED_KYC_DOCUMENT_TYPES.stream().allMatch(type -> {
                            KycDocument document = latestDocuments.get(type);

                            return document != null && "APPROVED".equalsIgnoreCase(
                                    document.getVerificationStatus());
                        });

        if (!approved) {
            throw new RuntimeException("KYC Pending. Eligibility check is blocked until all required KYC documents are approved.");
        }
    }

    private record Decision(String eligibilityStatus, String decision, String riskCategory,
            String borrowingLimit, BigDecimal loanAmount, String rejectionReason) {
    }
}
