package com.loanapp.loan_application.service.loan;

import com.loanapp.loan_application.dto.loan.DisbursementRequest;
import com.loanapp.loan_application.dto.loan.DisbursementResponse;
import com.loanapp.loan_application.entity.loan.Disbursement;
import org.springframework.data.domain.Page;

public interface DisbursementService {
    Disbursement createDisbursement(long LoanId);
    DisbursementResponse disburse(DisbursementRequest request);
    Page<DisbursementResponse> getAllDisburse(int size, int page);

}
