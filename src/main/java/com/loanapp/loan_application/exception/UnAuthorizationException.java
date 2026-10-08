package com.loanapp.loan_application.exception;

public class UnAuthorizationException extends RuntimeException {
    public UnAuthorizationException(String message) {
        super(message);
    }
}
