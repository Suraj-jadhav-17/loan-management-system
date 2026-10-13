package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.LoanAccountRequest;
import com.loanapp.loan_application.dto.loan.LoanAccountResponse;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.loan.DisbursementRepo;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.loan.LoanAccountService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LoanAccountServiceImpl implements LoanAccountService {
    private final CustomerRepository customerRepo;
    private final LoanDealRepo dealRepo;
    private final LoanAccountRepository accountRepo;
    private final DisbursementRepo disRepo;
    private final ModelMapper mapper;

    @Override
    public LoanAccountResponse createLoanAccount(LoanAccountRequest request) {
        Customer customer = customerRepo.findById(request.getCustomerId()).orElseThrow(()->new ResourceNotFoundException("Customer not found"));
        LoanDeal deal = dealRepo.findById(request.getLoanDealId()).orElseThrow(()->new ResourceNotFoundException("LoanDeal not found"));

        LoanAccount account = LoanAccount.builder()
                .loanAccountNo("LON"+ UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .customer(customer)
                .disbursementDate(disRepo.getDisbursementByLoanId(deal.getId()).getDisbursementDate())
                .emiAmount(deal.getEmiAmount())
                .interestRate(deal.getInterestRate())
                .loanAmount(deal.getAmount())
                .loanDeal(deal)
                .loanStatus(LoanStatus.ACTIVE)
                .outstandingPrincipal(deal.getAmount())
                .tenureMonths(deal.getTenureMonths())
                .totalPaidAmount(BigDecimal.ZERO)
                .build();

        LoanAccount saved = accountRepo.save(account);
        return mapper.map(saved, LoanAccountResponse.class) ;
    }



    @Override
    public Page<LoanAccountResponse> getAllLoanAccount(int size, int page) {
        return accountRepo.findAll(PageRequest.of(page,size)).map(obj->mapper.map(obj, LoanAccountResponse.class));
    }

    @Override
    public LoanAccountResponse getLoanAccountByLoanDeal(Long loanDealId) {
      LoanAccount acc= accountRepo.getLoanAccountByLoanDeal_Id(loanDealId);
        return mapper.map(acc, LoanAccountResponse.class);
    }

    @Override
    public Page<LoanAccountResponse> getLoanAccountsByCustomer(Long customerId,int size,int page) {
        return accountRepo.getLoanAccountByCustomer_CustomerId(customerId,PageRequest.of(page,size)).map(obj->mapper.map(obj, LoanAccountResponse.class));
    }
}























