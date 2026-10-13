package com.loanapp.loan_application.service.loan;

import com.loanapp.loan_application.dto.loan.LoanAccountRequest;
import com.loanapp.loan_application.dto.loan.LoanAccountResponse;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LoanAccountService {
     LoanAccountResponse createLoanAccount(LoanAccountRequest request);
     Page<LoanAccountResponse>  getAllLoanAccount(int size, int page);
     LoanAccountResponse getLoanAccountByLoanDeal(Long LoanDealId);
     Page<LoanAccountResponse> getLoanAccountsByCustomer(Long CustomerId,int size,int page);
}
