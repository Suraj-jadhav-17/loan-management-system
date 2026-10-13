package com.loanapp.loan_application.dto.register;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
public class LoginDto {

    private String email;
    private String password;
}
