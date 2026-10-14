package com.loanapp.loan_application.serviceimpl.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycCustomerSummaryResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentReviewRequestDto;
import com.loanapp.loan_application.dto.kycdocument.KycStatusResponseDto;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.file.LocalFileStorageService;
import com.loanapp.loan_application.service.kycdocument.LoanOfficerKycDocumentService;
import com.loanapp.loan_application.service.mail.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class LoanOfficerKycDocumentServiceImpl implements LoanOfficerKycDocumentService {
    private static final Logger log = LoggerFactory.getLogger(LoanOfficerKycDocumentServiceImpl.class);
    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";
    private static final List<String> REQUIRED_TYPES = List.of("Aadhaar Card", "PAN Card", "Salary Slip");

    private final KycDocumentRepository kycRepository;
    private final CustomerRepository customerRepository;
    private final LocalFileStorageService fileStorage;
    private final EmailService emailService;

    public LoanOfficerKycDocumentServiceImpl(KycDocumentRepository kycRepository,
                                              CustomerRepository customerRepository,
                                              LocalFileStorageService fileStorage,
                                              EmailService emailService) {
        this.kycRepository = kycRepository;
        this.customerRepository = customerRepository;
        this.fileStorage = fileStorage;
        this.emailService = emailService;
    }

    @Override
    public List<KycCustomerSummaryResponseDto> getCustomersWithKycDocuments() {
        List<KycCustomerSummaryResponseDto> result = new ArrayList<>();
        for (Long customerId : kycRepository.findDistinctCustomerIds()) {
            Customer customer = customerRepository.findById(customerId).orElse(null);
            if (customer == null) continue;
            KycStatusResponseDto status = buildStatus(customerId);
            KycCustomerSummaryResponseDto dto = new KycCustomerSummaryResponseDto();
            dto.setCustomerId(customerId);
            dto.setCustomerName((customer.getFirstName() == null ? "" : customer.getFirstName()) + " "
                    + (customer.getLastName() == null ? "" : customer.getLastName()).trim());
            dto.setEmail(customer.getEmail());
            dto.setMobile(customer.getMobileNo());
            dto.setKycStatus(status.getStatus());
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<KycDocumentResponseDto> getCustomerDocuments(Long customerId) {
        requireCustomer(customerId);
        return kycRepository.findByCustomerIdOrderByDocumentIdDesc(customerId)
                .stream().map(this::toOfficerResponse).toList();
    }

    @Override
    public Resource getCustomerDocumentFile(Long customerId, Long documentId) {
        requireCustomer(customerId);
        KycDocument doc = kycRepository.findByDocumentIdAndCustomerId(documentId, customerId)
                .orElseThrow(() -> new NoSuchElementException("KYC document was not found for this customer."));
        return fileStorage.loadAsResource(doc.getFilePath());
    }

    @Override
    @Transactional
    public void approveDocuments(Long customerId, KycDocumentReviewRequestDto request) {
        requireCustomer(customerId);
        List<KycDocument> documents = loadSelectedPendingDocuments(customerId, request);
        String oldStatus = buildStatus(customerId).getStatus();
        documents.forEach(doc -> {
            doc.setVerificationStatus(APPROVED);
            doc.setRejectionReason(null);
        });
        kycRepository.saveAll(documents);
        kycRepository.flush();
        String newStatus = buildStatus(customerId).getStatus();
        if (!"VERIFIED".equals(oldStatus) && "VERIFIED".equals(newStatus)) {
            Customer customer = requireCustomer(customerId);
            String subject = "KYC Verification Successful";
            String body = "Dear " + customer.getFirstName() + ",\n\n"
                    + "Your KYC document verification has been completed successfully. "
                    + "All required documents have been approved.\n\nThank you.";
            sendNotificationSafely(customer.getEmail(), subject, body);
        }
    }

    @Override
    @Transactional
    public void rejectDocuments(Long customerId, KycDocumentReviewRequestDto request) {
        Customer customer = requireCustomer(customerId);
        if (request == null || !StringUtils.hasText(request.getRejectionReason())) {
            throw new IllegalArgumentException("Rejection reason is required.");
        }
        List<KycDocument> documents = loadSelectedPendingDocuments(customerId, request);
        String reason = request.getRejectionReason().trim();
        List<String> types = new ArrayList<>();
        for (KycDocument doc : documents) {
            doc.setVerificationStatus(REJECTED);
            doc.setRejectionReason(reason);
            types.add(doc.getDocumentType());
        }
        kycRepository.saveAll(documents);
        String subject = "KYC Verification Failed - Action Required";
        String body = "Dear " + customer.getFirstName() + ",\n\n"
                + "We could not approve the following KYC document(s): " + String.join(", ", types) + ".\n\n"
                + "Reason: " + reason + "\n\n"
                + "Please log in to your loan application portal and upload a clear/correct replacement document.\n\nThank you.";
        sendNotificationSafely(customer.getEmail(), subject, body);
    }

    private List<KycDocument> loadSelectedPendingDocuments(Long customerId, KycDocumentReviewRequestDto request) {
        if (request == null || request.getDocumentIds() == null || request.getDocumentIds().isEmpty()) {
            throw new IllegalArgumentException("Select at least one document.");
        }
        List<Long> ids = request.getDocumentIds();
        if (new HashSet<>(ids).size() != ids.size() || ids.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Document IDs must be unique and valid.");
        }
        List<KycDocument> docs = kycRepository.findAllById(ids);
        if (docs.size() != ids.size()) throw new NoSuchElementException("One or more KYC documents were not found.");
        for (KycDocument doc : docs) {
            if (!customerId.equals(doc.getCustomerId())) {
                throw new IllegalArgumentException("A selected document does not belong to this customer.");
            }
            if (!PENDING.equalsIgnoreCase(doc.getVerificationStatus())) {
                throw new IllegalArgumentException("Only PENDING documents can be approved or rejected.");
            }
        }
        return docs;
    }

    private KycStatusResponseDto buildStatus(Long customerId) {
        Map<String, KycDocument> latest = new LinkedHashMap<>();
        for (KycDocument doc : kycRepository.findByCustomerIdOrderByDocumentIdDesc(customerId)) {
            latest.putIfAbsent(doc.getDocumentType(), doc);
        }
        int approved = 0, pending = 0, rejected = 0;
        for (KycDocument doc : latest.values()) {
            if (APPROVED.equalsIgnoreCase(doc.getVerificationStatus())) approved++;
            else if (PENDING.equalsIgnoreCase(doc.getVerificationStatus())) pending++;
            else if (REJECTED.equalsIgnoreCase(doc.getVerificationStatus())) rejected++;
        }
        String status = approved == REQUIRED_TYPES.size() ? "VERIFIED" : rejected > 0 ? "REUPLOAD_REQUIRED" : "PENDING";
        KycStatusResponseDto dto = new KycStatusResponseDto();
        dto.setCustomerId(customerId); dto.setStatus(status); dto.setTotalDocuments(latest.size());
        dto.setApprovedDocuments(approved); dto.setPendingDocuments(pending); dto.setRejectedDocuments(rejected);
        dto.setRequiredDocuments(REQUIRED_TYPES.size());
        return dto;
    }

    private Customer requireCustomer(Long customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NoSuchElementException("Customer was not found."));
    }

    private KycDocumentResponseDto toOfficerResponse(KycDocument doc) {
        KycDocumentResponseDto dto = new KycDocumentResponseDto();
        dto.setDocumentId(doc.getDocumentId()); dto.setCustomerId(doc.getCustomerId());
        dto.setDocumentType(doc.getDocumentType()); dto.setVerificationStatus(doc.getVerificationStatus());
        dto.setRejectionReason(doc.getRejectionReason());
        dto.setFileUrl("/auth/v1/loan-office/kyc/customers/" + doc.getCustomerId()
                + "/documents/" + doc.getDocumentId() + "/file");
        return dto;
    }

    private void sendNotificationSafely(String to, String subject, String body) {
        try {
            emailService.sendNotificationEmail(to, subject, body);
        } catch (MailException | IllegalStateException ex) {
            // DB approval/rejection remains saved; an email failure is logged for retry/operations follow-up.
            log.error("KYC status saved, but notification email could not be sent to {}", to, ex);
        }
    }
}
