package com.loanapp.loan_application.service.loanOfficer;

import com.loanapp.loan_application.response.CustomerResponse;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.Page;

public interface LoanOfficerService {
    Page<CustomerResponse> getAllLoans(@Min(0) int page, @Min(0) @Max(10) int limit, String sortBy, String direction);
}
