package com.loanapp.loan_application.repository.emischedulerepository;


import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmiScheduleRepository extends JpaRepository<EmiSchedules, Long> {

    List<EmiSchedules> findByLoanAccountId(Long loanAccountId);

    boolean existsByLoanAccountId(Long loanAccountId);

    List<EmiSchedules> findByLoanAccountIdAndPaymentStatus(Long loanAccountId, PaymentStatus paymentStatus);

    List<EmiSchedules> findByLoanAccountIdInAndPaymentStatusAndDueDateGreaterThanEqualOrderByDueDateAsc(List<Long> loanAccountIds, PaymentStatus paymentStatus, LocalDate dueDate);

}