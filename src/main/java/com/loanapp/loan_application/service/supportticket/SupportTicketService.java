package com.loanapp.loan_application.service.supportticket;

import com.loanapp.loan_application.dto.supportticket.*;

import java.util.List;

public interface SupportTicketService {

    SupportTicketResponseDto createTicket(
            SupportTicketRequestDto request);

    SupportTicketResponseDto getTicket(
            Integer ticketId);

    List<SupportTicketResponseDto> getCustomerTickets(
            Integer customerId);

    List<SupportTicketResponseDto> getLoanTickets(
            Integer loanAccountId);

    List<SupportTicketResponseDto> getOfficerTickets(
            String status);

    void updateStatus(
            Integer ticketId,
            String status);
}