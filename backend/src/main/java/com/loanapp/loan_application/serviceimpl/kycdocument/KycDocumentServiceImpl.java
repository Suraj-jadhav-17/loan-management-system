package com.loanapp.loan_application.serviceimpl.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.*;
import com.loanapp.loan_application.entity.kycdocument.KycDocument;
import com.loanapp.loan_application.entity.Customer;
import com.loanapp.loan_application.repository.kycdocument.KycDocumentRepository;
import com.loanapp.loan_application.repository.CustomerRepository;
import com.loanapp.loan_application.service.kycdocument.KycDocumentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
public class KycDocumentServiceImpl implements KycDocumentService {

    private static final List<String> REQUIRED_DOCUMENT_TYPES =
            List.of(
                    "Aadhaar Card",
                    "PAN Card",
                    "Salary Slip"
            );

    private static final String PENDING = "PENDING";
    private static final String APPROVED = "APPROVED";
    private static final String REJECTED = "REJECTED";

    private final KycDocumentRepository kycDocumentRepository;
    private final CustomerRepository customerRepository;

    @Value("${app.file-storage.kyc-dir:uploads/kyc}")
    private String kycStorageDirectory;

    public KycDocumentServiceImpl(
            KycDocumentRepository kycDocumentRepository,
            CustomerRepository customerRepository) {

        this.kycDocumentRepository = kycDocumentRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<String> getAvailableDocumentTypes(Integer customerId) {

        List<KycDocument> documents =
                kycDocumentRepository
                        .findByCustomerIdOrderByDocumentIdDesc(customerId);

        Map<String, KycDocument> latestDocuments = new HashMap<>();

        for (KycDocument document : documents) {

            latestDocuments.putIfAbsent(
                    document.getDocumentType(),
                    document
            );
        }

        return REQUIRED_DOCUMENT_TYPES.stream()
                .filter(type -> {

                    KycDocument document =
                            latestDocuments.get(type);

                    return document == null ||
                            REJECTED.equalsIgnoreCase(
                                    document.getVerificationStatus()
                            );
                })
                .toList();
    }

    @Override
    public KycDocumentResponseDto uploadDocument(
            Integer customerId,
            KycDocumentRequestDto request) {

        validateDocumentType(request.getDocumentType());

        MultipartFile file = request.getFile();

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Please select a document file."
            );
        }

        List<KycDocument> existingDocuments =
                kycDocumentRepository
                        .findByCustomerIdOrderByDocumentIdDesc(customerId);

        for (KycDocument document : existingDocuments) {

            if (document.getDocumentType()
                    .equalsIgnoreCase(request.getDocumentType())
                    &&
                    !REJECTED.equalsIgnoreCase(
                            document.getVerificationStatus())) {

                throw new IllegalArgumentException(
                        "This document type has already been uploaded."
                );
            }
        }

        try {

            String originalFileName =
                    StringUtils.cleanPath(
                            Objects.requireNonNull(
                                    file.getOriginalFilename()
                            )
                    );

            String safeFileName =
                    Paths.get(originalFileName)
                            .getFileName()
                            .toString();

            String storedFileName =
                    UUID.randomUUID() + "_" + safeFileName;

            Path customerDirectory =
                    Paths.get(
                            kycStorageDirectory,
                            String.valueOf(customerId)
                    );

            Files.createDirectories(customerDirectory);

            Path targetFile =
                    customerDirectory.resolve(storedFileName);

            file.transferTo(targetFile.toFile());

            KycDocument document =
                    new KycDocument();

            document.setCustomerId(customerId);
            document.setDocumentType(
                    request.getDocumentType()
            );
            document.setFilePath(
                    targetFile.toString()
            );
            document.setVerificationStatus(PENDING);

            KycDocument saved =
                    kycDocumentRepository.save(document);

            return mapToResponse(saved);

        } catch (IOException e) {

            throw new RuntimeException(
                    "Unable to store KYC document.",
                    e
            );
        }
    }

    @Override
    public List<KycDocumentResponseDto> getCustomerDocuments(
            Integer customerId) {

        return kycDocumentRepository
                .findByCustomerIdOrderByDocumentIdDesc(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public KycStatusResponseDto getKycStatus(
            Integer customerId) {

        List<KycDocument> documents =
                kycDocumentRepository
                        .findByCustomerIdOrderByDocumentIdDesc(customerId);

        Map<String, KycDocument> latestDocuments = new HashMap<>();

        for (KycDocument document : documents) {

            latestDocuments.putIfAbsent(
                    document.getDocumentType(),
                    document
            );
        }

        int approved = 0;
        int pending = 0;
        int rejected = 0;

        for (KycDocument document : latestDocuments.values()) {

            String status =
                    document.getVerificationStatus();

            if (APPROVED.equalsIgnoreCase(status)) {
                approved++;
            } else if (PENDING.equalsIgnoreCase(status)) {
                pending++;
            } else if (REJECTED.equalsIgnoreCase(status)) {
                rejected++;
            }
        }

        String overallStatus;

        if (approved == REQUIRED_DOCUMENT_TYPES.size()) {
            overallStatus = "VERIFIED";

        } else if (rejected > 0) {
            overallStatus = "REUPLOAD_REQUIRED";

        } else {
            overallStatus = "PENDING";
        }

        KycStatusResponseDto response =
                new KycStatusResponseDto();

        response.setCustomerId(customerId);
        response.setStatus(overallStatus);
        response.setTotalDocuments(
                latestDocuments.size()
        );
        response.setApprovedDocuments(approved);
        response.setPendingDocuments(pending);
        response.setRejectedDocuments(rejected);
        response.setRequiredDocuments(
                REQUIRED_DOCUMENT_TYPES.size()
        );

        return response;
    }

    @Override
    public void deleteDocument(Integer documentId) {

        KycDocument document =
                kycDocumentRepository
                        .findById(documentId)
                        .orElseThrow(() ->
                                new NoSuchElementException(
                                        "KYC document not found."
                                )
                        );

        if (!PENDING.equalsIgnoreCase(
                document.getVerificationStatus())) {

            throw new IllegalArgumentException(
                    "Only pending documents can be deleted."
            );
        }

        kycDocumentRepository.delete(document);
    }

    @Override
    public List<KycCustomerSummaryResponseDto> getOfficerCustomers() {

        List<Integer> customerIds =
                kycDocumentRepository
                        .findDistinctCustomerIds();

        List<KycCustomerSummaryResponseDto> response =
                new ArrayList<>();

        for (Integer customerId : customerIds) {

            Optional<Customer> customer =
                    customerRepository.findById(customerId);

            if (customer.isEmpty()) {
                continue;
            }

            Customer c = customer.get();

            KycStatusResponseDto status =
                    getKycStatus(customerId);

            KycCustomerSummaryResponseDto dto =
                    new KycCustomerSummaryResponseDto();

            dto.setCustomerId(customerId);
            dto.setCustomerName(
                    c.getFirstName() + " " +
                            (c.getLastName() == null
                                    ? ""
                                    : c.getLastName())
            );
            dto.setEmail(c.getEmail());
            dto.setMobile(c.getMobileNo());
            dto.setKycStatus(status.getStatus());

            response.add(dto);
        }

        return response;
    }

    @Override
    public List<KycDocumentResponseDto>
    getCustomerDocumentsForOfficer(Integer customerId) {

        return getCustomerDocuments(customerId);
    }

    @Override
    public void approveDocuments(
            Integer customerId,
            KycDocumentReviewRequestDto request) {

        validateDocumentIds(request);

        List<KycDocument> documents =
                kycDocumentRepository.findAllById(
                        request.getDocumentIds()
                );

        validateCustomerDocuments(
                customerId,
                request.getDocumentIds(),
                documents
        );

        for (KycDocument document : documents) {

            if (!PENDING.equalsIgnoreCase(
                    document.getVerificationStatus())) {

                throw new IllegalArgumentException(
                        "Only pending documents can be approved."
                );
            }

            document.setVerificationStatus(APPROVED);
        }

        String oldStatus =
                getKycStatus(customerId).getStatus();

        kycDocumentRepository.saveAll(documents);

        String newStatus =
                getKycStatus(customerId).getStatus();

        /*
         * When newStatus becomes VERIFIED for the first time,
         * send success email here.
         */

        if (!"VERIFIED".equals(oldStatus)
                && "VERIFIED".equals(newStatus)) {

            sendVerificationSuccessEmail(customerId);
        }
    }

    @Override
    public void rejectDocuments(
            Integer customerId,
            KycDocumentReviewRequestDto request) {

        validateDocumentIds(request);

        if (!StringUtils.hasText(
                request.getRejectionReason())) {

            throw new IllegalArgumentException(
                    "Rejection reason is required."
            );
        }

        List<KycDocument> documents =
                kycDocumentRepository.findAllById(
                        request.getDocumentIds()
                );

        validateCustomerDocuments(
                customerId,
                request.getDocumentIds(),
                documents
        );

        List<String> rejectedDocumentNames =
                new ArrayList<>();

        for (KycDocument document : documents) {

            if (!PENDING.equalsIgnoreCase(
                    document.getVerificationStatus())) {

                throw new IllegalArgumentException(
                        "Only pending documents can be rejected."
                );
            }

            document.setVerificationStatus(REJECTED);

            rejectedDocumentNames.add(
                    document.getDocumentType()
            );
        }

        kycDocumentRepository.saveAll(documents);

        /*
         * Send customer email.
         */
        sendRejectionEmail(
                customerId,
                rejectedDocumentNames,
                request.getRejectionReason()
        );
    }

    private void validateDocumentType(
            String documentType) {

        if (!REQUIRED_DOCUMENT_TYPES
                .contains(documentType)) {

            throw new IllegalArgumentException(
                    "Invalid KYC document type. Allowed values: "
                            + REQUIRED_DOCUMENT_TYPES
            );
        }
    }

    private void validateDocumentIds(
            KycDocumentReviewRequestDto request) {

        if (request.getDocumentIds() == null ||
                request.getDocumentIds().isEmpty()) {

            throw new IllegalArgumentException(
                    "Please select at least one document."
            );
        }
    }

    private void validateCustomerDocuments(
            Integer customerId,
            List<Integer> documentIds,
            List<KycDocument> documents) {

        if (documents.size() != documentIds.size()) {

            throw new IllegalArgumentException(
                    "One or more documents were not found."
            );
        }

        for (KycDocument document : documents) {

            if (!customerId.equals(
                    document.getCustomerId())) {

                throw new IllegalArgumentException(
                        "Document does not belong to customer."
                );
            }
        }
    }

    private KycDocumentResponseDto mapToResponse(
            KycDocument document) {

        KycDocumentResponseDto response =
                new KycDocumentResponseDto();

        response.setDocumentId(
                document.getDocumentId()
        );
        response.setCustomerId(
                document.getCustomerId()
        );
        response.setDocumentType(
                document.getDocumentType()
        );
        response.setFilePath(
                document.getFilePath()
        );
        response.setFileUrl(
                "/api/kyc/documents/"
                        + document.getDocumentId()
                        + "/file"
        );
        response.setVerificationStatus(
                document.getVerificationStatus()
        );

        return response;
    }

    private void sendVerificationSuccessEmail(
            Integer customerId) {

        /*
         * Connect JavaMailSender here.
         */
    }

    private void sendRejectionEmail(
            Integer customerId,
            List<String> documents,
            String reason) {

        /*
         * Connect JavaMailSender here.
         */
    }
}