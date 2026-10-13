package com.loanapp.loan_application.serviceimpl.user;

import com.loanapp.loan_application.dto.customer.CustomerProfileResponse;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.user.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepo;

    public UserServiceImpl(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

}
