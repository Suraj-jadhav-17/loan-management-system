package com.loanapp.loan_application.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {


    private String status;
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String path;
    private Object metadata;


}
