package com.loanapp.loan_application.dto.user;

import lombok.Data;

@Data
public class UserProfileResponse {

    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String mobileNo;
    private String roleName;
}