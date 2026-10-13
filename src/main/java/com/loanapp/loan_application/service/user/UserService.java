package com.loanapp.loan_application.service.user;

import com.loanapp.loan_application.dto.user.UserProfileResponse;
import com.loanapp.loan_application.entity.register.User;

public interface UserService {
    UserProfileResponse findByProfile(String email);
}
