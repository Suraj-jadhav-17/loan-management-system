package com.loanapp.loan_application.dto.register;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthFlowResponse {

    private String status;
    private String message;
    private String email;
    private String verificationToken;
    private String setupUri;
    private String qrCodeDataUrl;
    private String accessToken;
    private String refreshToken;
}
