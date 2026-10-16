
        package com.loanapp.loan_application.serviceimpl.register;

import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class TotpService {

    private final GoogleAuthenticator googleAuthenticator =
            new GoogleAuthenticator();

    public String generateSecret() {
        GoogleAuthenticatorKey key =
                googleAuthenticator.createCredentials();

        return key.getKey();
    }

    public boolean verifyOtp(String secret, String otp) {
        if (secret == null || secret.isBlank()
                || otp == null || !otp.matches("\\d{6}")) {
            return false;
        }

        try {
            return googleAuthenticator.authorize(
                    secret,
                    Integer.parseInt(otp)
            );
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public String generateQrCodeDataUrl(String otpUri) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(
                    otpUri, BarcodeFormat.QR_CODE, 260, 260,
                    Map.of(EncodeHintType.MARGIN, 1)
            );
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", output);
            return "data:image/png;base64," + Base64.getEncoder().encodeToString(output.toByteArray());
        } catch (Exception ex) {
            throw new IllegalStateException("Could not generate authenticator QR code", ex);
        }
    }

    public String generateOtpUri(String email, String secret) {
        String issuer = "LoanManagementSystem";

        String encodedIssuer =
                URLEncoder.encode(issuer, StandardCharsets.UTF_8);

        String encodedEmail =
                URLEncoder.encode(email, StandardCharsets.UTF_8);

        return "otpauth://totp/"
                + encodedIssuer + ":" + encodedEmail
                + "?secret=" + secret
                + "&issuer=" + encodedIssuer
                + "&algorithm=SHA1"
                + "&digits=6"
                + "&period=30";
    }
}

