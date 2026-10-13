package com.loanapp.loan_application.serviceimpl.supportticket;

import com.loanapp.loan_application.dto.supportticket.*;
import com.loanapp.loan_application.entity.supportticket.SupportTicket;
import com.loanapp.loan_application.repository.supportticket.SupportTicketRepository;
import com.loanapp.loan_application.service.supportticket.SupportTicketService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class SupportTicketServiceImpl implements SupportTicketService {

    private static final String OPEN = "OPEN";
    private static final String IN_PROGRESS = "IN_PROGRESS";
    private static final String RESOLVED = "RESOLVED";
    private static final String CLOSED = "CLOSED";

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicketServiceImpl(SupportTicketRepository supportTicketRepository) {
        this.supportTicketRepository = supportTicketRepository;
    }

    @Override
    public SupportTicketResponseDto createTicket(SupportTicketRequestDto request) {

        if (request.getCustomerId() == null) {
            throw new IllegalArgumentException("Customer ID is required.");
        }

        if (!StringUtils.hasText(request.getSubject())) {
            throw new IllegalArgumentException("Subject is required.");
        }

        if (!StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("Description is required.");
        }

        SupportTicket ticket = new SupportTicket();

        ticket.setCustomerId(request.getCustomerId());
        ticket.setLoanAccountId(request.getLoanAccountId());
        ticket.setSubject(request.getSubject());
        ticket.setDescription(request.getDescription());
        ticket.setStatus(OPEN);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setUpdatedAt(LocalDateTime.now());

        SupportTicket saved = supportTicketRepository.save(ticket);

        return mapToResponse(saved);
    }

    @Override
    public SupportTicketResponseDto getTicket(
            Integer ticketId) {

        SupportTicket ticket =
                supportTicketRepository
                        .findById(ticketId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Support ticket not found."
                                )
                        );

        return mapToResponse(ticket);
    }

    @Override
    public List<SupportTicketResponseDto>
    getCustomerTickets(Integer customerId) {

        return supportTicketRepository
                .findByCustomerIdOrderByCreatedAtDesc(
                        customerId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SupportTicketResponseDto>
    getLoanTickets(Integer loanAccountId) {

        return supportTicketRepository
                .findByLoanAccountIdOrderByCreatedAtDesc(
                        loanAccountId
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<SupportTicketResponseDto>
    getOfficerTickets(String status) {

        List<SupportTicket> tickets;

        if (StringUtils.hasText(status)) {

            validateStatus(status);

            tickets =
                    supportTicketRepository
                            .findByStatusOrderByCreatedAtDesc(
                                    status
                            );

        } else {

            tickets =
                    supportTicketRepository
                            .findAllByOrderByCreatedAtDesc();
        }

        return tickets.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void updateStatus(
            Integer ticketId,
            String status) {

        validateStatus(status);

        SupportTicket ticket =
                supportTicketRepository
                        .findById(ticketId)
                        .orElseThrow(() ->
                                new RuntimeException("Support ticket not found."));

        ticket.setStatus(status);
        ticket.setUpdatedAt(LocalDateTime.now());

        supportTicketRepository.save(ticket);
    }

    private void validateStatus(String status) {

        if (!OPEN.equals(status)
                && !IN_PROGRESS.equals(status)
                && !RESOLVED.equals(status)
                && !CLOSED.equals(status)) {

            throw new IllegalArgumentException("Invalid support ticket status.");
        }
    }

    private SupportTicketResponseDto mapToResponse(SupportTicket ticket) {

        SupportTicketResponseDto response = new SupportTicketResponseDto();

        response.setTicketId(ticket.getTicketId());
        response.setCustomerId(ticket.getCustomerId());
        response.setLoanAccountId(ticket.getLoanAccountId());
        response.setSubject(ticket.getSubject());
        response.setDescription(ticket.getDescription());
        response.setStatus(ticket.getStatus());
        response.setCreatedAt(ticket.getCreatedAt());
        response.setUpdatedAt(ticket.getUpdatedAt());

        return response;
    }
}