package com.loanapp.loan_application.repository.supportticket;

import com.loanapp.loan_application.entity.supportticket.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportTicketRepository
        extends JpaRepository<SupportTicket, Integer> {

    List<SupportTicket> findByCustomerIdOrderByCreatedAtDesc(
            Integer customerId);

    List<SupportTicket> findByLoanAccountIdOrderByCreatedAtDesc(
            Integer loanAccountId);

    List<SupportTicket> findByStatusOrderByCreatedAtDesc(
            String status);

    List<SupportTicket> findAllByOrderByCreatedAtDesc();
}