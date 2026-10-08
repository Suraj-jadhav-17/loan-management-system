package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.ScoreCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoreCardServiceImpl implements ScoreCardService {

    private final ScoreCardRepo scoreCardRepo;
    private final CustomerRepository customerRepository;
    private final CIBILReportRepo cibilReportRepo;

    @Override
    @CacheEvict(value = "latestScoreCard", key = "#request.customerId")
    public ScoreCardResponseDto createScoreCard(ScoreCardRequestDto request) {

        log.info("Creating scorecard for customerId={}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException(
                        "Customer not found with id: " + request.getCustomerId()));

        CIBILReport report = cibilReportRepo
                .findTopByCustomerCustomerIdOrderByCheckDateDesc(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException(
                        "CIBIL report not found for customer: " + request.getCustomerId()));

        validateCustomerData(customer);

        int incomeScore = calculateIncomeScore(customer.getMonthlyIncome());
        int employmentScore = calculateEmploymentScore(customer.getEmploymentType());
        int ageScore = calculateAgeScore(customer.getAge());

        BigDecimal foir = calculateFoir(
                customer.getMonthlyInvestment(),
                customer.getMonthlyIncome());

        int foirScore = calculateFoirScore(foir);
        int totalScore = incomeScore + employmentScore + ageScore + foirScore;

        EligibilityDecision decision = getDecision(report.getCibilScore());

        ScoreCard scoreCard = ScoreCard.builder()
                .customer(customer)
                .currentStatus(decision.status())
                .rejectionReason(decision.rejectionReason())
                .appliedDate(LocalDateTime.now())
                .cibilScore(report.getCibilScore())
                .riskCategory(decision.riskCategory())
                .monthlyIncome(customer.getMonthlyIncome())
                .employmentType(customer.getEmploymentType())
                .age(customer.getAge())
                .monthlyInvestment(customer.getMonthlyInvestment())
                .foir(foir)
                .incomeScore(incomeScore)
                .employmentScore(employmentScore)
                .ageScore(ageScore)
                .foirScore(foirScore)
                .totalScore(totalScore)
                .eligibleLoanAmount(decision.loanAmount())
                .build();

        return mapToResponse(scoreCardRepo.save(scoreCard));
    }

    @Override
    @Cacheable(value = "latestScoreCard", key = "#customerId")
    public ScoreCardResponseDto getLatestScoreCard(Long customerId) {

        ScoreCard scoreCard = scoreCardRepo
                .findTopByCustomerCustomerIdOrderByScoreCardIdDesc(customerId)
                .orElseThrow(() -> new RuntimeException(
                        "Scorecard not found for customer: " + customerId));

        return mapToResponse(scoreCard);
    }

    @Override
    public Page<ScoreCardResponseDto> getScoreCardHistory(
            Long customerId,
            Pageable pageable) {

        return scoreCardRepo
                .findByCustomerCustomerIdOrderByScoreCardIdDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    private int calculateIncomeScore(BigDecimal income) {

        if (income == null || income.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        if (income.compareTo(BigDecimal.valueOf(25_000)) < 0) {
            return 100;
        }

        if (income.compareTo(BigDecimal.valueOf(50_000)) <= 0) {
            return 200;
        }

        if (income.compareTo(BigDecimal.valueOf(100_000)) <= 0) {
            return 300;
        }

        return 400;
    }

    private int calculateEmploymentScore(String employmentType) {

        if (employmentType == null || employmentType.isBlank()) {
            return 0;
        }

        String employment = employmentType.trim().toLowerCase();

        if (employment.equals("government")) {
            return 200;
        }

        if (employment.equals("private") || employment.equals("private sector")) {
            return 150;
        }

        if (employment.equals("self-employed")
                || employment.equals("self employed")
                || employment.equals("business")) {
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

    private BigDecimal calculateFoir(
            BigDecimal totalMonthlyDebtPayment,
            BigDecimal monthlyIncome) {

        if (monthlyIncome == null ||
                monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Monthly income must be greater than zero");
        }

        if (totalMonthlyDebtPayment == null ||
                totalMonthlyDebtPayment.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(
                    "Monthly investment / total monthly obligation cannot be negative");
        }

        return totalMonthlyDebtPayment
                .divide(monthlyIncome, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private int calculateFoirScore(BigDecimal foir) {

        if (foir.compareTo(BigDecimal.valueOf(30)) < 0) {
            return 250;
        }

        if (foir.compareTo(BigDecimal.valueOf(50)) <= 0) {
            return 150;
        }

        if (foir.compareTo(BigDecimal.valueOf(60)) <= 0) {
            return 75;
        }

        return 0;
    }

    private EligibilityDecision getDecision(Integer score) {

        if (score == null || score < 300 || score > 900) {
            throw new RuntimeException("CIBIL score must be between 300 and 900");
        }

        if (score >= 900) {
            return new EligibilityDecision(
                    "AUTO_APPROVE",
                    "Excellent",
                    "Above ₹1 Crore",
                    null,
                    null);
        }

        if (score >= 800) {
            return new EligibilityDecision(
                    "AUTO_APPROVE",
                    "Very Good",
                    "₹75 Lakh to ₹1 Crore",
                    BigDecimal.valueOf(10_000_000),
                    null);
        }

        if (score >= 750) {
            return new EligibilityDecision(
                    "MANUAL_REVIEW",
                    "Good",
                    "₹50 Lakh to ₹75 Lakh",
                    BigDecimal.valueOf(7_500_000),
                    "Manual officer review required.");
        }

        if (score >= 700) {
            return new EligibilityDecision(
                    "MANUAL_REVIEW",
                    "Average",
                    "₹25 Lakh to ₹50 Lakh",
                    BigDecimal.valueOf(5_000_000),
                    "Manual officer review required.");
        }

        if (score >= 650) {
            return new EligibilityDecision(
                    "MANUAL_REVIEW",
                    "Risky",
                    "₹10 Lakh to ₹25 Lakh",
                    BigDecimal.valueOf(2_500_000),
                    "Manual officer review required.");
        }

        return new EligibilityDecision(
                "AUTO_REJECT",
                "Reject",
                "Less than ₹10 Lakh",
                BigDecimal.ZERO,
                "CIBIL score is below 650.");
    }

    private void validateCustomerData(Customer customer) {

        if (customer.getAge() == null) {
            throw new RuntimeException("Customer age is required");
        }

        if (customer.getEmploymentType() == null ||
                customer.getEmploymentType().isBlank()) {
            throw new RuntimeException("Customer employment type is required");
        }

        if (customer.getMonthlyIncome() == null ||
                customer.getMonthlyIncome().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Customer monthly income must be greater than zero");
        }

        if (customer.getMonthlyInvestment() == null ||
                customer.getMonthlyInvestment().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException(
                    "Customer monthly investment / total monthly obligation is required");
        }
    }

    private ScoreCardResponseDto mapToResponse(ScoreCard scoreCard) {

        return ScoreCardResponseDto.builder()
                .scoreCardId(scoreCard.getScoreCardId())
                .customerId(scoreCard.getCustomer().getCustomerId())
                .currentStatus(scoreCard.getCurrentStatus())
                .rejectionReason(scoreCard.getRejectionReason())
                .appliedDate(scoreCard.getAppliedDate())
                .cibilScore(scoreCard.getCibilScore())
                .riskCategory(scoreCard.getRiskCategory())
                .monthlyIncome(scoreCard.getMonthlyIncome())
                .employmentType(scoreCard.getEmploymentType())
                .age(scoreCard.getAge())
                .monthlyInvestment(scoreCard.getMonthlyInvestment())
                .foir(scoreCard.getFoir())
                .incomeScore(scoreCard.getIncomeScore())
                .employmentScore(scoreCard.getEmploymentScore())
                .ageScore(scoreCard.getAgeScore())
                .foirScore(scoreCard.getFoirScore())
                .totalScore(scoreCard.getTotalScore())
                .eligibleLoanAmount(scoreCard.getEligibleLoanAmount())
                .build();
    }

    private record EligibilityDecision(
            String status,
            String riskCategory,
            String borrowingLimit,
            BigDecimal loanAmount,
            String rejectionReason) {
    }
}
