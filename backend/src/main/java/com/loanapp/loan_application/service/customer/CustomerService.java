package com.loanapp.loan_application.service.customer;

import com.loanapp.loan_application.dto.cuatomer.CustomerProfileResponse;
import com.loanapp.loan_application.dto.cuatomer.EmiSummaryResponse;
import com.loanapp.loan_application.dto.dashBoard.CustomerDashboardResponse;
import com.loanapp.loan_application.dto.payment.LoanPaymentRecordDto;

import java.util.List;


public interface CustomerService {
    CustomerProfileResponse findByProfile(String email);

    CustomerProfileResponse updateProfile(String email, CustomerProfileResponse response);

    List<CustomerProfileResponse> getAllCustomerList();

  CustomerDashboardResponse getDashboard(String email);

    EmiSummaryResponse getEmiSummary(String email);

    LoanPaymentRecordDto getReciptsPayment(String email);
}