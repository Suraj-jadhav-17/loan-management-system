package com.loanapp.loan_application.service;

import com.loanapp.loan_application.dto.RegistrationRequest;
import com.loanapp.loan_application.entity.User;
import jakarta.validation.Valid;

public interface AuthService {
    User register(@Valid RegistrationRequest registrationRequest);
}
