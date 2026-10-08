package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.ScoreCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreCardServiceImpl implements ScoreCardService {

    private final ScoreCardRepo scoreCardRepo;
    private final CustomerRepository customerRepository;
    private final CIBILReportRepo cibilReportRepo;
    private final KycDocumentRepository kycDocumentRepository;
    private final ModelMapper modelMapper;

    private static final List<String> REQUIRED_KYC_DOCUMENT_TYPES =
            List.of("Aadhaar Card", "PAN Card", "Salary Slip");

    @Override
    @CacheEvict(value = "latestScoreCard", key = "#request.customerId")
    public ScoreCardResponseDto createScoreCard(ScoreCardRequestDto request) {

        log.info("Creating scorecard for customerId={}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));


        validateKycApproved(request.getCustomerId());


        CIBILReport report = cibilReportRepo.findTopByCustomerCustomerIdOrderByCheckDateDesc(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("CIBIL report not found for customer: " + request.getCustomerId()));

        validateCustomerData(customer);

        int incomeScore = calculateIncomeScore(customer.getMonthlyIncome());
        int employmentScore = calculateEmploymentScore(customer.getEmploymentType());
        int ageScore = calculateAgeScore(customer.getAge());
        BigDecimal foir = calculateFoir(customer.getMonthlyInvestment(), customer.getMonthlyIncome());
        int foirScore = calculateFoirScore(foir);
        int totalScore = incomeScore + employmentScore + ageScore + foirScore;

        BigDecimal eligibleLoanAmount = calculateEligibleLoanAmount(report.getCibilScore());

        String riskCategory = calculateRiskCategory(report.getCibilScore(), totalScore);

        ScoreCard scoreCard = ScoreCard.builder().customer(customer)

                        .currentStatus("ELIGIBLE")
                        .rejectionReason(null)
                        .appliedDate(LocalDateTime.now())
                .cibilScore(report.getCibilScore())
                .riskCategory(riskCategory)
                        .eligibleLoanAmount(
                                eligibleLoanAmount
                        )

                        .foir(foir)
                        .incomeScore(incomeScore)
                        .employmentScore(employmentScore)
                        .ageScore(ageScore)
                        .foirScore(foirScore)
                        .totalScore(totalScore)
                .build();

        ScoreCard savedScoreCard = scoreCardRepo.save(scoreCard);

        ScoreCardResponseDto response = modelMapper.map(
                        savedScoreCard, ScoreCardResponseDto.class);

        response.setCustomerId(savedScoreCard.getCustomer().getCustomerId());

        return response;
    }

    @Override
    @Cacheable(value = "latestScoreCard", key = "#customerId")
    public ScoreCardResponseDto getLatestScoreCard(Long customerId) {

        ScoreCard scoreCard = scoreCardRepo.findTopByCustomerCustomerIdOrderByScoreCardIdDesc(customerId)
                        .orElseThrow(() -> new RuntimeException("Scorecard not found for customer: " + customerId));

        ScoreCardResponseDto response = modelMapper.map(scoreCard, ScoreCardResponseDto.class);

        response.setCustomerId(scoreCard.getCustomer().getCustomerId());

        return response;
    }

    @Override
    public Page<ScoreCardResponseDto> getScoreCardHistory(Long customerId, Pageable pageable) {

        return scoreCardRepo.findByCustomerCustomerIdOrderByScoreCardIdDesc(customerId, pageable)
                .map(scoreCard -> {

                    ScoreCardResponseDto response =
                            modelMapper.map(scoreCard, ScoreCardResponseDto.class);

                    response.setCustomerId(scoreCard.getCustomer().getCustomerId());

                    return response;
                });
    }



    private void validateKycApproved(Long customerId) {

        List<KycDocument> documents =
                kycDocumentRepository.findByCustomerIdOrderByDocumentIdDesc(Math.toIntExact(customerId));

        Map<String, KycDocument> latestDocuments = new HashMap<>();

        for (KycDocument document : documents) {

            latestDocuments.putIfAbsent(document.getDocumentType(), document);
        }



        boolean approved = REQUIRED_KYC_DOCUMENT_TYPES.stream().allMatch(type -> {

                            KycDocument document = latestDocuments.get(type);

                            return document != null
                                    && "APPROVED".equalsIgnoreCase(
                                    document.getVerificationStatus()
                            );
                        });

        if (!approved) {

            throw new RuntimeException(
                    "KYC Pending. Scorecard calculation is blocked until all required KYC documents are approved."
            );
        }
    }



    private int calculateIncomeScore(
            BigDecimal income) {

        if (income == null ||
                income.compareTo(BigDecimal.ZERO) <= 0) {

            return 0;
        }

        if (income.compareTo(
                BigDecimal.valueOf(25_000)) < 0) {

            return 100;
        }

        if (income.compareTo(
                BigDecimal.valueOf(50_000)) <= 0) {

            return 200;
        }

        if (income.compareTo(
                BigDecimal.valueOf(100_000)) <= 0) {

            return 300;
        }

        return 400;
    }

    private int calculateEmploymentScore(
            String employmentType) {

        if (employmentType == null || employmentType.isBlank()) {
            return 0;
        }

        String employment = employmentType.trim()
                .toLowerCase();

        if (employment.equals("government")) {
            return 200;
        }

        if (employment.equals("private") ||
                employment.equals("private sector")) {

            return 150;
        }

        if (employment.equals("self-employed") ||
                employment.equals("self employed") ||
                employment.equals("business")) {

            return 100;
        }

        return 0;
    }


    private int calculateAgeScore(Long age) {

        if (age == null) {
            return 0;
        }

        if (age >= 21 && age <= 24) {
            return 50;
        }

        if (age >= 25 && age <= 45) {
            return 150;
        }

        if (age >= 46 && age <= 60) {
            return 100;
        }

        return 0;
    }


    private BigDecimal calculateFoir(BigDecimal totalMonthlyDebtPayment, BigDecimal monthlyIncome) {

        if (monthlyIncome == null ||
                monthlyIncome.compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Monthly income must be greater than zero"
            );
        }

        if (totalMonthlyDebtPayment == null ||
                totalMonthlyDebtPayment.compareTo(
                        BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Monthly investment / total monthly obligation "
                            + "cannot be negative"
            );
        }

        return totalMonthlyDebtPayment
                .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP
                );
    }



    private int calculateFoirScore(
            BigDecimal foir) {

        if (foir.compareTo(
                BigDecimal.valueOf(30)) < 0) {

            return 250;
        }

        if (foir.compareTo(
                BigDecimal.valueOf(50)) <= 0) {

            return 150;
        }

        if (foir.compareTo(
                BigDecimal.valueOf(60)) <= 0) {

            return 75;
        }

        return 0;
    }


    private BigDecimal calculateEligibleLoanAmount(
            Integer cibilScore) {

        if (cibilScore == null ||
                cibilScore < 300 ||
                cibilScore > 900) {

            throw new RuntimeException("CIBIL score must be between 300 and 900");
        }

        if (cibilScore >= 900) {
            return null;
        }

        if (cibilScore >= 800) {
            return BigDecimal.valueOf(10_000_000);
        }

        if (cibilScore >= 750) {
            return BigDecimal.valueOf(7_500_000);
        }

        if (cibilScore >= 700) {
            return BigDecimal.valueOf(5_000_000);
        }

        if (cibilScore >= 650) {
            return BigDecimal.valueOf(2_500_000);
        }

        return BigDecimal.ZERO;
    }


    private String calculateRiskCategory(Integer cibilScore, Integer totalScore) {

        if (cibilScore == null) {
            return "HIGH";
        }

        if (cibilScore >= 800 && totalScore >= 700) {
            return "LOW";
        }

        if (cibilScore >= 750 && totalScore >= 600) {
            return "LOW";
        }

        if (cibilScore >= 700 && totalScore >= 500) {
            return "MEDIUM";
        }

        if (cibilScore >= 650 && totalScore >= 400) {
            return "MEDIUM";
        }

        return "HIGH";
    }


    private void validateCustomerData(Customer customer) {

        if (customer.getAge() == null) {
            throw new RuntimeException("Customer age is required");
        }

        if (customer.getEmploymentType() == null ||
                customer.getEmploymentType().isBlank()) {

            throw new RuntimeException(
                    "Customer employment type is required"
            );
        }

        if (customer.getMonthlyIncome() == null ||
                customer.getMonthlyIncome().compareTo(
                        BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Customer monthly income must be greater than zero"
            );
        }

        if (customer.getMonthlyInvestment() == null ||
                customer.getMonthlyInvestment().compareTo(
                        BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Customer monthly investment / total monthly obligation "
                            + "is required"
            );
        }
    }
}