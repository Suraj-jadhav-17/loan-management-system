package com.loanapp.loan_application.service.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;
import org.springframework.core.io.Resource;
import java.util.List;

public interface LoanOfficerSupportTicketService {
    SupportTicketResponseDto getTicket(Long ticketId);
    List<SupportTicketResponseDto> getTickets(String status);
    List<SupportTicketResponseDto> getTicketsForLoanAccount(Long loanAccountId);
    void updateStatus(Long ticketId, String status);
    Resource getTicketAttachment(Long ticketId);
}
