
package com.loanapp.loan_application.dto.kycdocument;

import jakarta.validation.constraints.NotBlank;
import org.springframework.web.multipart.MultipartFile;

public class KycDocumentRequestDto {

    @NotBlank(message = "Document type is required")
    private String documentType;

    private MultipartFile file;

    public KycDocumentRequestDto() {
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
