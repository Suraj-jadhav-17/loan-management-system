package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.EligibilityResultRequestDto;
import com.loanapp.loan_application.dto.cibil.EligibilityResultResponseDto;
import com.loanapp.loan_application.entity.Customer;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.cibil.EligibilityResult;
import com.loanapp.loan_application.repository.CustomerRepository;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.cibil.EligibilityResultRepo;
import com.loanapp.loan_application.service.cibil.EligibilityResultService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EligibilityResultServiceImpl
        implements EligibilityResultService {

    private final EligibilityResultRepo eligibilityResultRepo;

    private final CIBILReportRepo cibilReportRepo;

    private final CustomerRepository customerRepository;

    @Override
    public EligibilityResultResponseDto checkEligibility(EligibilityResultRequestDto request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        validateRequest(request, customer);

        CIBILReport latestCibilReport = cibilReportRepo.findTopByCustomerCustomerIdOrderByCheckDateDesc(request.getCustomerId())
                        .orElseThrow(() -> new RuntimeException("CIBIL report not found for customer: " + request.getCustomerId()));

        Integer cibilScore = latestCibilReport.getCibilScore();

        if (cibilScore == null) {throw new RuntimeException("CIBIL score is not available");
        }



        if (cibilScore < 300 || cibilScore > 900) {

            throw new RuntimeException("CIBIL score must be between 300 and 900");
        }



        String riskCategory = getRiskCategory(cibilScore);



        Boolean isEligible;

        String rejectionReason = null;

        BigDecimal loanAmount = null;



        if (cibilScore < 650) {

            isEligible = false;

            rejectionReason = "CIBIL score is below 650. " + "Applicant is not eligible for loan.";}



        else if (cibilScore <= 800) {

            isEligible = null;

            rejectionReason = "Manual officer review required.";}



        else {

            isEligible = true;
        }



        int incomeScore = calculateIncomeScore(customer.getMonthlyIncome()
                );

        int employmentScore = calculateEmploymentScore(customer.getEmploymentType());

        BigDecimal totalMonthlyDebtPayment = request.getTotalMonthlyDebtPayment();

        BigDecimal foir = calculateFoir(totalMonthlyDebtPayment, customer.getMonthlyIncome());

        int foirScore = calculateFoirScore(foir);



        int ageScore = 0;

        int totalScore = incomeScore + employmentScore + ageScore + foirScore;



        EligibilityResult result =
                EligibilityResult.builder()
                        .customer(customer)
                        .cibilScore(cibilScore)
                        .isEligible(isEligible)
                        .loanAmount(loanAmount)
                        .rejectionReason(rejectionReason)
                        .build();

        EligibilityResult savedResult = eligibilityResultRepo.save(result);

        return mapToResponse(savedResult);
    }

    @Override
    public EligibilityResultResponseDto getLatestEligibility(
            Long customerId
    ) {

        EligibilityResult result = eligibilityResultRepo.findTopByCustomerCustomerIdOrderByIdDesc(customerId)
                        .orElseThrow(() -> new RuntimeException("Eligibility result not found for customer: " + customerId));

        return mapToResponse(result);
    }



    private String getRiskCategory(Integer cibilScore
    ) {

        if (cibilScore >= 900) {
            return "Excellent";
        }

        if (cibilScore >= 800) {
            return "Very Good";
        }

        if (cibilScore >= 750) {
            return "Good";
        }

        if (cibilScore >= 700) {
            return "Average";
        }

        if (cibilScore >= 650) {
            return "Risky";
        }

        return "Reject";
    }



    private int calculateIncomeScore(BigDecimal monthlyIncome) {

        if (monthlyIncome == null) {
            return 0;
        }

        if (monthlyIncome.compareTo(
                BigDecimal.valueOf(25000)
        ) < 0) {

            return 100;
        }

        if (monthlyIncome.compareTo(
                BigDecimal.valueOf(50000)
        ) <= 0) {

            return 200;
        }

        if (monthlyIncome.compareTo(
                BigDecimal.valueOf(100000)
        ) <= 0) {

            return 300;
        }

        return 400;
    }



    private int calculateEmploymentScore(String employmentType
    ) {

        if (employmentType == null || employmentType.trim().isEmpty()) {

            return 0;
        }

        String employment = employmentType.trim().toLowerCase();

        if (employment.equals("government")) {
            return 200;
        }

        if (employment.equals("private")) {
            return 150;
        }

        if (employment.equals("self-employed")
                || employment.equals("self employed")
                || employment.equals("business")) {

            return 100;
        }

        return 0;
    }



    private BigDecimal calculateFoir(
            BigDecimal totalMonthlyDebtPayment,
            BigDecimal monthlyIncome
    ) {

        if (monthlyIncome == null ||
                monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {

            throw new RuntimeException(
                    "Monthly income must be greater than zero"
            );
        }

        if (totalMonthlyDebtPayment == null ||
                totalMonthlyDebtPayment.compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Total monthly debt payment cannot be negative"
            );
        }

        return totalMonthlyDebtPayment
                .divide(
                        monthlyIncome,
                        4,
                        java.math.RoundingMode.HALF_UP
                )
                .multiply(BigDecimal.valueOf(100));
    }



    private int calculateFoirScore(
            BigDecimal foir
    ) {

        if (foir.compareTo(
                BigDecimal.valueOf(30)
        ) < 0) {

            return 250;
        }

        if (foir.compareTo(
                BigDecimal.valueOf(50)
        ) <= 0) {

            return 150;
        }

        if (foir.compareTo(
                BigDecimal.valueOf(60)
        ) <= 0) {

            return 75;
        }

        return 0;
    }



    private void validateRequest(
            EligibilityResultRequestDto request,
            Customer customer
    ) {

        if (request.getCustomerId() == null) {

            throw new RuntimeException(
                    "Customer id is required"
            );
        }

        if (request.getTotalMonthlyDebtPayment() == null) {

            throw new RuntimeException(
                    "Total monthly debt payment is required"
            );
        }

        if (request.getTotalMonthlyDebtPayment()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new RuntimeException(
                    "Total monthly debt payment cannot be negative"
            );
        }

        if (customer.getMonthlyIncome() == null) {

            throw new RuntimeException(
                    "Monthly income is not available for customer"
            );
        }
    }



    private EligibilityResultResponseDto mapToResponse(EligibilityResult result) {

        return EligibilityResultResponseDto.builder()
                .id(result.getId())
                .customerId(
                        result.getCustomer()
                                .getCustomerId()
                )
                .cibilScore(result.getCibilScore())
                .isEligible(result.getIsEligible())
                .loanAmount(result.getLoanAmount())
                .rejectionReason(result.getRejectionReason())
                .build();
    }
}