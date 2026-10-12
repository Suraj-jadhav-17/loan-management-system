package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CIBILReportServiceImpl
        implements CIBILReportService {

    private final CIBILReportRepo cibilReportRepo;
    private final CustomerRepository customerRepository;
    private final KycDocumentRepository kycDocumentRepository;
    private final ModelMapper modelMapper;

    private static final List<String> REQUIRED_KYC_DOCUMENT_TYPES =
            List.of(
                    "Aadhaar Card",
                    "PAN Card",
                    "Salary Slip"
            );

    @Override
    @CacheEvict(
            value = "latestCibilReport",
            key = "#request.customerId"
    )
    public CIBILReportResponseDto createReport(
            CIBILReportRequestDto request) {

        log.info(
                "Creating CIBIL report for customerId={}",
                request.getCustomerId()
        );

        Customer customer =
                customerRepository
                        .findById(request.getCustomerId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Customer not found with id: "
                                                + request.getCustomerId()
                                )
                        );

        validateKycApproved(request.getCustomerId());

        if (request.getCibilScore() == null ||
                request.getCibilScore() < 300 ||
                request.getCibilScore() > 900) {

            throw new RuntimeException(
                    "CIBIL score must be between 300 and 900"
            );
        }

        String panNo =
                request.getPanNo() == null ||
                        request.getPanNo().isBlank()
                        ? customer.getPanCard()
                        : request.getPanNo();

        CIBILReport report =
                CIBILReport.builder()
                        .customer(customer)
                        .panNo(panNo)
                        .cibilScore(request.getCibilScore())
                        .checkDate(LocalDateTime.now())
                        .build();

        CIBILReport savedReport =
                cibilReportRepo.save(report);

        CIBILReportResponseDto response =
                modelMapper.map(
                        savedReport,
                        CIBILReportResponseDto.class
                );

        response.setCustomerId(
                savedReport
                        .getCustomer()
                        .getCustomerId()
        );

        return response;
    }

    @Override
    @Cacheable(
            value = "latestCibilReport",
            key = "#customerId"
    )
    public CIBILReportResponseDto getLatestReport(
            Long customerId) {

        CIBILReport report =
                cibilReportRepo
                        .findTopByCustomerCustomerIdOrderByCheckDateDesc(
                                customerId
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "CIBIL report not found for customer: "
                                                + customerId
                                )
                        );

        CIBILReportResponseDto response =
                modelMapper.map(
                        report,
                        CIBILReportResponseDto.class
                );

        response.setCustomerId(
                report
                        .getCustomer()
                        .getCustomerId()
        );

        return response;
    }

    @Override
    public Page<CIBILReportResponseDto> getReportHistory(
            Long customerId,
            Pageable pageable) {

        return cibilReportRepo
                .findByCustomerCustomerIdOrderByCheckDateDesc(
                        customerId,
                        pageable
                )
                .map(report -> {

                    CIBILReportResponseDto response =
                            modelMapper.map(
                                    report,
                                    CIBILReportResponseDto.class
                            );

                    response.setCustomerId(
                            report
                                    .getCustomer()
                                    .getCustomerId()
                    );

                    return response;
                });
    }

    private void validateKycApproved(Long customerId) {

        List<KycDocument> documents =
                kycDocumentRepository
                        .findByCustomerIdOrderByDocumentIdDesc(
                                Math.toIntExact(customerId)
                        );

        Map<String, KycDocument> latestDocuments =
                new HashMap<>();

        for (KycDocument document : documents) {

            latestDocuments.putIfAbsent(
                    document.getDocumentType(),
                    document
            );
        }

        boolean approved =
                REQUIRED_KYC_DOCUMENT_TYPES.stream()
                        .allMatch(type -> {

                            KycDocument document =
                                    latestDocuments.get(type);

                            return document != null
                                    && "APPROVED".equalsIgnoreCase(
                                    document.getVerificationStatus()
                            );
                        });

        if (!approved) {

            throw new RuntimeException(
                    "KYC Pending. Please complete and get all required KYC documents approved before calculating CIBIL score."
            );
        }
    }
}