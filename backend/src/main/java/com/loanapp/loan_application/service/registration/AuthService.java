package com.loanapp.loan_application.service.registration;

import com.loanapp.loan_application.dto.register.AuthFlowResponse;
import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.dto.register.VerifyOtpRequest;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.response.LoginResponse;

public interface AuthService {

    User register(RegistrationRequest request);

    AuthFlowResponse login(LoginDto loginDto);

    boolean checkOtp(String email, VerifyOtpRequest otp);

    AuthFlowResponse setupAuthenticator(String verificationToken);

    LoginResponse verifyAuthenticatorSetup(String verificationToken, String otp);

    LoginResponse verifyAuthenticatorLogin(String verificationToken, String otp);

    String requestRecoveryOtp(String verificationToken);

    AuthFlowResponse verifyRecoveryOtp(String verificationToken, String otp);

    AuthFlowResponse resetAuthenticator(String verificationToken);
}

