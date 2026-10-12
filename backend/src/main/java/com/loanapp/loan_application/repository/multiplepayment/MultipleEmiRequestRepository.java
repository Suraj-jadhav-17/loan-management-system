package com.loanapp.loan_application.repository.multiplepayment;

import com.loanapp.loan_application.entity.multiplepayment.MultipleEmiRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MultipleEmiRequestRepository
        extends JpaRepository<MultipleEmiRequest, Long> {

    List<MultipleEmiRequest> findByLoanAccountId(Long loanAccountId);
    List<MultipleEmiRequest> findByApprovalStatus(String approvalStatus);
}