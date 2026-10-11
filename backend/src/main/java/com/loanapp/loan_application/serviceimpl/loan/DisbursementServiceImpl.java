package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.dto.loan.DisbursementRequest;
import com.loanapp.loan_application.dto.loan.DisbursementResponse;
import com.loanapp.loan_application.entity.loan.Disbursement;
import com.loanapp.loan_application.entity.loan.DisbursementStatus;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.loan.DisbursementRepo;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.service.loan.DisbursementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DisbursementServiceImpl implements DisbursementService {
    private final DisbursementRepo disRepo;
    private final LoanDealRepo loanRepo;
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
        return ;
    }

    @Override
    public Page<DisbursementResponse> getPendingDisburse(int size, int page) {
        return null;
    }
}
