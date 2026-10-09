package com.loanapp.loan_application.serviceimpl.cibil;

import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
import com.loanapp.loan_application.entity.cibil.CIBILReport;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.CIBILReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CIBILReportServiceImpl implements CIBILReportService {

    private final CIBILReportRepo cibilReportRepo;
    private final CustomerRepository customerRepository;

    @Override
    @CacheEvict(value = "latestCibilReport", key = "#request.customerId")
    public CIBILReportResponseDto createReport(CIBILReportRequestDto request) {

        log.info("Creating CIBIL report for customerId={}", request.getCustomerId());

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found with id: " + request.getCustomerId()));

        if (request.getCibilScore() == null ||
                request.getCibilScore() < 300 ||
                request.getCibilScore() > 900) {
            throw new RuntimeException("CIBIL score must be between 300 and 900");
        }

        String panNo = request.getPanNo() == null || request.getPanNo().isBlank()
                ? customer.getPanCard()
                : request.getPanNo();

        CIBILReport report = CIBILReport.builder()
                .customer(customer)
                .panNo(panNo)
                .cibilScore(request.getCibilScore())
                .checkDate(LocalDateTime.now())
                .build();

        return mapToResponse(cibilReportRepo.save(report));
    }

    @Override
    @Cacheable(value = "latestCibilReport", key = "#customerId")
    public CIBILReportResponseDto getLatestReport(Long customerId) {

        CIBILReport report = cibilReportRepo.findTopByCustomerCustomerIdOrderByCheckDateDesc(customerId)
                .orElseThrow(() -> new RuntimeException("CIBIL report not found for customer: " + customerId));

        return mapToResponse(report);
    }

    @Override
    public Page<CIBILReportResponseDto> getReportHistory(Long customerId, Pageable pageable) {

        return cibilReportRepo.findByCustomerCustomerIdOrderByCheckDateDesc(customerId, pageable)
                .map(this::mapToResponse);
    }

    private CIBILReportResponseDto mapToResponse(CIBILReport report) {
        return CIBILReportResponseDto.builder()
                .id(report.getId())
                .customerId(report.getCustomer().getCustomerId())
                .panNo(report.getPanNo())
                .cibilScore(report.getCibilScore())
                .checkDate(report.getCheckDate())
                .build();
    }
}
