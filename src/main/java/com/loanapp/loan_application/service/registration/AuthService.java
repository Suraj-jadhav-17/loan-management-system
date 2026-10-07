package com.loanapp.loan_application.service.registration;

import com.loanapp.loan_application.dto.RegistrationRequest;
import com.loanapp.loan_application.entity.registration.User;
import jakarta.validation.Valid;

public interface AuthService {
    User register(@Valid RegistrationRequest registrationRequest);
}
