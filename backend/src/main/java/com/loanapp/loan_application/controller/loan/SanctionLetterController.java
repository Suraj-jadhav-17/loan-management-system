package com.loanapp.loan_application.controller.loan;

import com.loanapp.loan_application.entity.loan.SanctionLetter;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.loan.SanctionLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/v1/sanction-letter")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:4200/")
public class SanctionLetterController {

    private final SanctionLetterService sanctionLetterService;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SanctionLetter>> getSanctionLetterById(
            @PathVariable Long id) {

        SanctionLetter letter = sanctionLetterService.getSanctionLetterById(id);

        return ResponseEntity.ok(createApiResponse(
                letter,
                "Sanction letter retrieved successfully",
                "/v1/sanction-letter/" + id
        ));
    }

    @GetMapping("/deal/{loanDealId}/view")
    public ResponseEntity<ByteArrayResource> viewSanctionLetter(
            @PathVariable Long loanDealId) {

        byte[] pdf = sanctionLetterService.viewSanctionLetter(loanDealId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.inline()
                                .filename("sanction-letter-" + loanDealId + ".pdf")
                                .build()
                                .toString()
                )
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }

    @GetMapping("/deal/{loanDealId}/download")
    public ResponseEntity<ByteArrayResource> downloadSanctionLetter(
            @PathVariable Long loanDealId) {

        byte[] pdf = sanctionLetterService.downloadSanctionLetter(loanDealId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment()
                                .filename("sanction-letter-" + loanDealId + ".pdf")
                                .build()
                                .toString()
                )
                .contentLength(pdf.length)
                .body(new ByteArrayResource(pdf));
    }

    private <T> ApiResponse<T> createApiResponse(
            T data,
            String message,
            String path) {

        ApiResponse<T> response = new ApiResponse<>();
        response.setStatus("SUCCESS");
        response.setSuccess(true);
        response.setMessage(message);
        response.setData(data);
        response.setTimestamp(LocalDateTime.now());
        response.setPath(path);
        response.setMetadata(null);

        return response;
    }
}