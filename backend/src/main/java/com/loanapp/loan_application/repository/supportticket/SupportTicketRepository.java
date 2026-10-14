
package com.loanapp.loan_application.repository.supportticket;

import com.loanapp.loan_application.entity.supportticket.SupportTicket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SupportTicketRepository
        extends JpaRepository<SupportTicket, Long> {

    // Get all tickets created by a particular customer
    List<SupportTicket> findByCustomerIdOrderByCreatedAtDesc(
            Long customerId);

    // Get tickets related to a particular loan account
    List<SupportTicket> findByLoanAccountIdOrderByCreatedAtDesc(
            Long loanAccountId);

    // Get tickets filtered by status
    List<SupportTicket> findByStatusOrderByCreatedAtDesc(
            String status);

    // Get all tickets, newest first
    List<SupportTicket> findAllByOrderByCreatedAtDesc();

    // Find a ticket belonging to a particular customer
    Optional<SupportTicket> findByTicketIdAndCustomerId(
            Long ticketId,
            Long customerId);
}
