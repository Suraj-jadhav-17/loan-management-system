package com.loanapp.loan_application.serviceimpl.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;
import com.loanapp.loan_application.entity.supportticket.SupportTicket;
import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
import com.loanapp.loan_application.repository.supportticket.SupportTicketRepository;
import com.loanapp.loan_application.service.file.LocalFileStorageService;
import com.loanapp.loan_application.service.supportticket.LoanOfficerSupportTicketService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;

@Service
public class LoanOfficerSupportTicketServiceImpl implements LoanOfficerSupportTicketService {
    private static final Set<String> ALLOWED_STATUSES = Set.of("OPEN", "IN_PROGRESS", "RESOLVED", "CLOSED");
    private final SupportTicketRepository ticketRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final LocalFileStorageService fileStorage;

    public LoanOfficerSupportTicketServiceImpl(SupportTicketRepository ticketRepository,
                                               LoanAccountRepository loanAccountRepository,
                                               LocalFileStorageService fileStorage) {
        this.ticketRepository = ticketRepository;
        this.loanAccountRepository = loanAccountRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    public SupportTicketResponseDto getTicket(Long ticketId) {
        return toResponse(requireTicket(ticketId));
    }

    @Override
    public List<SupportTicketResponseDto> getTickets(String status) {
        if (!StringUtils.hasText(status)) {
            return ticketRepository.findAllByOrderByCreatedAtDesc().stream().map(this::toResponse).toList();
        }
        String normalized = status.trim().toUpperCase(Locale.ROOT);
        validateStatus(normalized);
        return ticketRepository.findByStatusOrderByCreatedAtDesc(normalized).stream().map(this::toResponse).toList();
    }

    @Override
    public List<SupportTicketResponseDto> getTicketsForLoanAccount(Long loanAccountId) {
        if (!loanAccountRepository.existsById(loanAccountId)) throw new NoSuchElementException("Loan account was not found.");
        return ticketRepository.findByLoanAccountIdOrderByCreatedAtDesc(loanAccountId)
                .stream().map(this::toResponse).toList();
    }

    @Override
    @Transactional
    public void updateStatus(Long ticketId, String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        validateStatus(normalized);
        SupportTicket ticket = requireTicket(ticketId);
        ticket.setStatus(normalized);
        ticket.setUpdatedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }

    @Override
    public Resource getTicketAttachment(Long ticketId) {
        return fileStorage.loadAsResource(requireTicket(ticketId).getAttachmentPath());
    }

    private SupportTicket requireTicket(Long ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NoSuchElementException("Support ticket was not found."));
    }

    private void validateStatus(String status) {
        if (!ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status. Allowed values: " + ALLOWED_STATUSES);
        }
    }

    private SupportTicketResponseDto toResponse(SupportTicket ticket) {
        SupportTicketResponseDto dto = new SupportTicketResponseDto();
        dto.setTicketId(ticket.getTicketId()); dto.setCustomerId(ticket.getCustomerId());
        dto.setLoanAccountId(ticket.getLoanAccountId()); dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription()); dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt()); dto.setUpdatedAt(ticket.getUpdatedAt());
        if (StringUtils.hasText(ticket.getAttachmentPath())) {
            dto.setAttachmentUrl("/auth/v1/loan-office/support/tickets/" + ticket.getTicketId() + "/attachment");
        }
        return dto;
    }
}
