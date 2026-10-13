package com.loanapp.loan_application.serviceimpl.user;

import com.loanapp.loan_application.dto.user.UserProfileResponse;
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

    @Override
    public UserProfileResponse findByProfile(String email) {
       User user = userRepo.findByEmail(email).orElseThrow(()->
                new RuntimeException("User Detail Not Exsits"));


        UserProfileResponse response = new UserProfileResponse();

        response.setUserId(user.getUserId());
        response.setFirstName(user.getFirstName());
        response.setLastName(user.getLastName());
        response.setEmail(user.getEmail());
        response.setMobileNo(user.getMobileNo());
        response.setRoleName(user.getRole().getRoleName());

        return response;
    }
}
