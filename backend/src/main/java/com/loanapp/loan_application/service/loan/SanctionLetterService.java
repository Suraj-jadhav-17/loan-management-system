package com.loanapp.loan_application.service.loan;

import com.loanapp.loan_application.entity.loan.SanctionLetter;

public interface SanctionLetterService {
    SanctionLetter createSanctionLetter(Long loanDealId);
    SanctionLetter getSanctionLetterById(Long id);
    byte[] downloadSanctionLetter(Long dealId);
    byte[] viewSanctionLetter(Long dealId);
}
