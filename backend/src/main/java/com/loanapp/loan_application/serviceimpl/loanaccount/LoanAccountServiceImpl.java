package com.loanapp.loan_application.serviceimpl.loanaccount;


import com.loanapp.loan_application.dto.request.loanaccount.LoanAccountRequestDto;
import com.loanapp.loan_application.dto.response.loanaccount.LoanAccountResponseDto;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
import com.loanapp.loan_application.service.loanaccount.LoanAccountService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class LoanAccountServiceImpl implements LoanAccountService {

    private final LoanAccountRepository loanAccountRepository;
    private final ModelMapper modelMapper;


    @Override
    public LoanAccountResponseDto createLoanAccount(LoanAccountRequestDto requestDto) {
        LoanAccount loanAccount = modelMapper.map(requestDto, LoanAccount.class);
        LoanAccount savedLoanAccount = loanAccountRepository.save(loanAccount);
        return modelMapper.map(savedLoanAccount, LoanAccountResponseDto.class);
    }


    @Override
    public LoanAccountResponseDto getLoanAccountById(Long loanAccountId) {
        LoanAccount loanAccount = loanAccountRepository.findById(loanAccountId).orElseThrow(() -> new RuntimeException("Loan account not found with id: " + loanAccountId));
        return modelMapper.map(loanAccount, LoanAccountResponseDto.class);
    }

}