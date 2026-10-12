package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.DisbursementRequest;
import com.loanapp.loan_application.dto.loan.DisbursementResponse;
import com.loanapp.loan_application.entity.loan.Disbursement;
import com.loanapp.loan_application.entity.loan.DisbursementStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.exception.InvalidInputException;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.loan.DisbursementRepo;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.service.loan.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;


import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.*;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {
    private final DisbursementRepo disRepo;
    private final LoanDealRepo loanRepo;
    private final ModelMapper mapper;
    @Override
    public Disbursement createDisbursement(long loanId) {
        LoanDeal loanDeal = loanRepo.findById(loanId).orElseThrow(()-> new ResourceNotFoundException("LoanDeal Not Found"));
        Disbursement disbursement =  Disbursement.builder()
                .disbursementDate(null)
                .bankPartner(null)
                .disburseAmount(loanDeal.getAmount())
                .loanDeal(loanDeal)
                .status(DisbursementStatus.PENDING)
                .build();
        return disRepo.save(disbursement);
    }

    @Override
    public DisbursementResponse disburse(DisbursementRequest request) {
        Disbursement disbursement = disRepo.findById(request.getDisbursementId()).orElseThrow(()-> new ResourceNotFoundException("Disbursement Not Found"));
        if(disbursement.getStatus() == DisbursementStatus.DISBURSED){
            throw new InvalidInputException("Already Disbursed");
        }
        int date =request.getDisbursementDate().getDayOfMonth();
        Month month =request.getDisbursementDate().getMonth();
        if(disbursement.getLoanDeal().getEmiDay().intValue()!=date){
            throw  new InvalidInputException("Emi Day Mismatch ...Emi Day is "+date);
        }
        if (!YearMonth.from(request.getDisbursementDate()).isAfter(YearMonth.from(LocalDate.now()))) {
            throw new InvalidInputException("Disbursement date must be in the next month or later");
        }
        if(request.getBankPartner()==null||request.getBankPartner().isBlank()){
            throw new InvalidInputException("Bank Partner Required");
        }
        disbursement.setStatus(DisbursementStatus.DISBURSED);
        disbursement.setBankPartner(request.getBankPartner());
        Disbursement saved = disRepo.save(disbursement);
        return mapper.map(saved, DisbursementResponse.class);
    }

    @Override
    public Page<DisbursementResponse> getAllDisburse(int size, int page) {
        return disRepo.findAll(PageRequest.of(page,size)).map(obj->mapper.map(obj, DisbursementResponse.class));
    }
}
