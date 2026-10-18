package com.loanapp.loan_application.controller.kycdocument;

import com.loanapp.loan_application.dto.kycdocument.KycDocumentRequestDto;
import com.loanapp.loan_application.dto.kycdocument.KycDocumentResponseDto;
import com.loanapp.loan_application.dto.kycdocument.KycStatusResponseDto;
import com.loanapp.loan_application.service.kycdocument.CustomerKycDocumentService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/auth/v1/customer/kyc")
@CrossOrigin("http://localhost:4200/")
public class CustomerKycDocumentController {
    private final CustomerKycDocumentService service;
    public CustomerKycDocumentController(CustomerKycDocumentService service) { this.service = service; }

    @GetMapping("/available-document-types")
    public ResponseEntity<List<String>> availableTypes(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.getAvailableDocumentTypes(email));
    }

    @PostMapping(value = "/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<KycDocumentResponseDto> upload(
            @AuthenticationPrincipal String email, @Valid @ModelAttribute KycDocumentRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.uploadDocument(email, request));
    }

    @GetMapping("/documents")
    public ResponseEntity<List<KycDocumentResponseDto>> myDocuments(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.getMyDocuments(email));
    }

    @GetMapping("/status")
    public ResponseEntity<KycStatusResponseDto> myStatus(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.getMyKycStatus(email));
    }

    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deletePending(@AuthenticationPrincipal String email, @PathVariable Long documentId) {
        service.deleteMyDocument(email, documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/documents/{documentId}/file")
    public ResponseEntity<Resource> viewFile(@AuthenticationPrincipal String email, @PathVariable Long documentId) {
        return inlineFile(service.getMyDocumentFile(email, documentId));
    }

    private ResponseEntity<Resource> inlineFile(Resource resource) {
        MediaType type = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(resource.getFilename() == null ? "document" : resource.getFilename(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString()).body(resource);
    }
}
