
package com.loanapp.loan_application.service.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketRequestDto;
import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;

import org.springframework.core.io.Resource;

import java.util.List;

public interface CustomerSupportTicketService {

    SupportTicketResponseDto createTicket(
            String customerEmail,
            SupportTicketRequestDto request
    );

    SupportTicketResponseDto getMyTicket(
            String customerEmail,
            Long ticketId
    );

    List<SupportTicketResponseDto> getMyTickets(String customerEmail);

    Resource getMyTicketAttachment(
            String customerEmail,
            Long ticketId
    );
}
