package com.loanapp.loan_application.service.penalty;

import com.loanapp.loan_application.dto.penalty.PenaltyChargesDto;

import java.util.List;

public interface PenaltyChargesService {

    PenaltyChargesDto add(PenaltyChargesDto dto);

    List<PenaltyChargesDto> getByLoan(Long loanAccountId);

    List<PenaltyChargesDto> getByEmi(Long emiScheduleId);
    void addOverdue();
}