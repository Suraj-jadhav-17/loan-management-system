package com.loanapp.loan_application.service.registration;

import com.loanapp.loan_application.dto.register.LoginDto;
import com.loanapp.loan_application.dto.register.RegistrationRequest;
import com.loanapp.loan_application.dto.register.VerifyOtpRequest;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.response.LoginResponse;
import jakarta.validation.Valid;

public interface AuthService {
    User register(@Valid RegistrationRequest registrationRequest);

    LoginResponse login(@Valid LoginDto loginDto);


    boolean checkOtp(String customerEmail, VerifyOtpRequest request);
}
