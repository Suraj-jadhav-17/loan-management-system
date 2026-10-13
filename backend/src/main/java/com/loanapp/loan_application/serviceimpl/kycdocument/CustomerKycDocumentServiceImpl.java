package com.loanapp.loan_application.serviceimpl.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycDocumentRequestDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycStatusResponseDto;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.file.LocalFileStorageService;
import com.loanapp.loan_application.service.kycdocument.CustomerKycDocumentService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class CustomerKycDocumentServiceImpl implements CustomerKycDocumentService {
    private static final List<String> REQUIRED_TYPES = List.of("Aadhaar Card", "PAN Card", "Salary Slip");
    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    private final KycDocumentRepository kycRepository;
    private final CustomerRepository customerRepository;
    private final LocalFileStorageService fileStorage;

    public CustomerKycDocumentServiceImpl(KycDocumentRepository kycRepository,
                                          CustomerRepository customerRepository,
                                          LocalFileStorageService fileStorage) {
        this.kycRepository = kycRepository;
        this.customerRepository = customerRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    public List<String> getAvailableDocumentTypes(String customerEmail) {
        Customer customer = requireCustomer(customerEmail);
        Map<String, KycDocument> latest = latestByType(customer.getCustomerId());
        return REQUIRED_TYPES.stream().filter(type -> {
            KycDocument doc = latest.get(type);
            return doc == null || REJECTED.equalsIgnoreCase(doc.getVerificationStatus());
        }).toList();
    }

    @Override
    @Transactional
    public KycDocumentResponseDto uploadDocument(String customerEmail, KycDocumentRequestDto request) {
        Customer customer = requireCustomer(customerEmail);
        if (request == null || !StringUtils.hasText(request.getDocumentType())) {
            throw new IllegalArgumentException("Document type is required.");
        }
        String documentType = normalizeDocumentType(request.getDocumentType());
        if (!getAvailableDocumentTypes(customerEmail).contains(documentType)) {
            throw new IllegalArgumentException("This document type is already uploaded and is not available for upload.");
        }

        MultipartFile file = request.getFile();
        String storedPath = fileStorage.store(file, "kyc", customer.getCustomerId());
        try {
            KycDocument document = new KycDocument();
            document.setCustomerId(customer.getCustomerId());
            document.setDocumentType(documentType);
            document.setFilePath(storedPath);
            document.setVerificationStatus(PENDING);
            document.setRejectionReason(null);
            return toResponse(kycRepository.save(document), false);
        } catch (RuntimeException ex) {
            fileStorage.deleteQuietly(storedPath);
            throw ex;
        }
    }

    @Override
    public List<KycDocumentResponseDto> getMyDocuments(String customerEmail) {
        Customer customer = requireCustomer(customerEmail);
        return kycRepository.findByCustomerIdOrderByDocumentIdDesc(customer.getCustomerId())
                .stream().map(doc -> toResponse(doc, false)).toList();
    }

    @Override
    public KycStatusResponseDto getMyKycStatus(String customerEmail) {
        Customer customer = requireCustomer(customerEmail);
        return buildStatus(customer.getCustomerId());
    }

    @Override
    @Transactional
    public void deleteMyDocument(String customerEmail, Long documentId) {
        Customer customer = requireCustomer(customerEmail);
        KycDocument document = kycRepository.findByDocumentIdAndCustomerId(documentId, customer.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("KYC document was not found for this customer."));
        if (!PENDING.equalsIgnoreCase(document.getVerificationStatus())) {
            throw new IllegalArgumentException("Only pending documents can be deleted by the customer.");
        }
        String path = document.getFilePath();
        kycRepository.delete(document);
        fileStorage.deleteQuietly(path);
    }

    @Override
    public Resource getMyDocumentFile(String customerEmail, Long documentId) {
        Customer customer = requireCustomer(customerEmail);
        KycDocument document = kycRepository.findByDocumentIdAndCustomerId(documentId, customer.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("KYC document was not found for this customer."));
        return fileStorage.loadAsResource(document.getFilePath());
    }

    public KycStatusResponseDto buildStatus(Long customerId) {
        Map<String, KycDocument> latest = latestByType(customerId);
        int approved = 0, pending = 0, rejected = 0;
        for (KycDocument doc : latest.values()) {
            String status = doc.getVerificationStatus() == null ? "" : doc.getVerificationStatus();
            if (APPROVED.equalsIgnoreCase(status)) approved++;
            else if (PENDING.equalsIgnoreCase(status)) pending++;
            else if (REJECTED.equalsIgnoreCase(status)) rejected++;
        }
        String overall;
        if (approved == REQUIRED_TYPES.size()) overall = "VERIFIED";
        else if (rejected > 0) overall = "REUPLOAD_REQUIRED";
        else overall = "PENDING";

        KycStatusResponseDto dto = new KycStatusResponseDto();
        dto.setCustomerId(customerId);
        dto.setStatus(overall);
        dto.setTotalDocuments(latest.size());
        dto.setApprovedDocuments(approved);
        dto.setPendingDocuments(pending);
        dto.setRejectedDocuments(rejected);
        dto.setRequiredDocuments(REQUIRED_TYPES.size());
        return dto;
    }

    public Map<String, KycDocument> latestByType(Long customerId) {
        Map<String, KycDocument> latest = new LinkedHashMap<>();
        for (KycDocument doc : kycRepository.findByCustomerIdOrderByDocumentIdDesc(customerId)) {
            latest.putIfAbsent(doc.getDocumentType(), doc);
        }
        return latest;
    }

    private Customer requireCustomer(String email) {
        if (!StringUtils.hasText(email)) throw new IllegalArgumentException("Authenticated customer email is missing.");
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Customer account was not found."));
    }

    private String normalizeDocumentType(String requested) {
        return REQUIRED_TYPES.stream().filter(type -> type.equalsIgnoreCase(requested.trim()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException(
                        "Invalid document type. Allowed types: " + REQUIRED_TYPES));
    }

    private KycDocumentResponseDto toResponse(KycDocument doc, boolean officerView) {
        KycDocumentResponseDto dto = new KycDocumentResponseDto();
        dto.setDocumentId(doc.getDocumentId());
        dto.setCustomerId(doc.getCustomerId());
        dto.setDocumentType(doc.getDocumentType());
        dto.setVerificationStatus(doc.getVerificationStatus());
        dto.setRejectionReason(doc.getRejectionReason());
        dto.setFileUrl(officerView
                ? "/auth/v1/loan-office/kyc/customers/" + doc.getCustomerId() + "/documents/" + doc.getDocumentId() + "/file"
                : "/auth/v1/customer/kyc/documents/" + doc.getDocumentId() + "/file");
        return dto;
    }
}
