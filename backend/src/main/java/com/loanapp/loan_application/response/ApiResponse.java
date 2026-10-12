package com.loanapp.loan_application.response;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    private String status;
    private boolean success;
    private String message;
    private T data;
}