
package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.cibil.ScoreCard;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.cibil.ScoreCardRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.service.cibil.CIBILReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CIBILReportServiceImpl implements CIBILReportService {

    private final CIBILReportRepo cibilReportRepo;
    private final ScoreCardRepo scoreCardRepo;
    private final CustomerRepository customerRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final ModelMapper modelMapper;

    private static final List<String> REQUIRED_KYC_DOCUMENT_TYPES =
            List.of("Aadhaar Card", "PAN Card", "Salary Slip");

    @Override
    @Transactional
    @CacheEvict(value = "latestCibilReport", key = "#request.customerId")
    public CIBILReportResponseDto createReport(CIBILReportRequestDto request) {

        Long customerId = request.getCustomerId();

        log.info("Creating internal credit report for customerId={}", customerId);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customerId));

        validateKycApproved(customerId);

        ScoreCard scoreCard = scoreCardRepo.findTopByCustomerCustomerIdOrderByScoreCardIdDesc(customerId)
                .orElseThrow(() -> new RuntimeException("Scorecard not found. Generate a scorecard first."));

        Integer calculatedScore = scoreCard.getCibilScore();

        if (calculatedScore == null || calculatedScore < 300 || calculatedScore > 900) {
            throw new RuntimeException("The calculated scorecard score is invalid.");
        }

        String panNo = customer.getPanCard();

        if (panNo == null || panNo.isBlank()) {
            throw new RuntimeException("PAN card is not available for this customer.");
        }

        CIBILReport report = CIBILReport.builder()
                .customer(customer)
                .panNo(panNo)
                .cibilScore(calculatedScore)
                .checkDate(LocalDateTime.now())
                .build();

        CIBILReport savedReport = cibilReportRepo.save(report);

        CIBILReportResponseDto response = modelMapper.map(savedReport, CIBILReportResponseDto.class);
        response.setCustomerId(customer.getCustomerId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "latestCibilReport", key = "#customerId")
    public CIBILReportResponseDto getLatestReport(Long customerId) {

        CIBILReport report = cibilReportRepo.findTopByCustomerCustomerIdOrderByCheckDateDesc(customerId)
                .orElseThrow(() -> new RuntimeException("CIBIL report not found for customer: " + customerId));

        CIBILReportResponseDto response = modelMapper.map(report, CIBILReportResponseDto.class);
        response.setCustomerId(report.getCustomer().getCustomerId());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CIBILReportResponseDto> getReportHistory(Long customerId, Pageable pageable) {

        return cibilReportRepo.findByCustomerCustomerIdOrderByCheckDateDesc(customerId, pageable)
                .map(report -> {CIBILReportResponseDto response = modelMapper.map(report, CIBILReportResponseDto.class);

                    response.setCustomerId(report.getCustomer().getCustomerId());
                    return response;
                });
    }

    private void validateKycApproved(Long customerId) {

        List<KycDocument> documents = kycDocumentRepository.findByCustomerIdOrderByDocumentIdDesc(customerId);

        Map<String, KycDocument> latestDocuments = new HashMap<>();

        for (KycDocument document : documents) {
            latestDocuments.putIfAbsent(document.getDocumentType(), document);
        }

        boolean approved = REQUIRED_KYC_DOCUMENT_TYPES.stream()
                .allMatch(type -> {
                    KycDocument document = latestDocuments.get(type);

                    return document != null
                            && "APPROVED".equalsIgnoreCase(
                            document.getVerificationStatus());
                });

        if (!approved) {
            throw new RuntimeException("KYC Pending. Please complete and get all required " + "KYC documents approved before generating a report.");
        }
    }
}
