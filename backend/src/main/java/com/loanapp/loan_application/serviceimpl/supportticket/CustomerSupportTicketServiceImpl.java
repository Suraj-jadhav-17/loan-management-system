package com.loanapp.loan_application.serviceimpl.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketRequestDto;
import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.entity.supportticket.SupportTicket;
import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.repository.supportticket.SupportTicketRepository;
import com.loanapp.loan_application.service.file.LocalFileStorageService;
import com.loanapp.loan_application.service.supportticket.CustomerSupportTicketService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CustomerSupportTicketServiceImpl implements CustomerSupportTicketService {
    private final CustomerRepository customerRepository;
    private final SupportTicketRepository ticketRepository;
    private final LoanAccountRepository loanAccountRepository;
    private final LocalFileStorageService fileStorage;

    public CustomerSupportTicketServiceImpl(CustomerRepository customerRepository,
                                            SupportTicketRepository ticketRepository,
                                            LoanAccountRepository loanAccountRepository,
                                            LocalFileStorageService fileStorage) {
        this.customerRepository = customerRepository;
        this.ticketRepository = ticketRepository;
        this.loanAccountRepository = loanAccountRepository;
        this.fileStorage = fileStorage;
    }

    @Override
    @Transactional
    public SupportTicketResponseDto createTicket(String customerEmail, SupportTicketRequestDto request) {
        Customer customer = requireCustomer(customerEmail);
        if (request == null || !StringUtils.hasText(request.getSubject()) || !StringUtils.hasText(request.getDescription())) {
            throw new IllegalArgumentException("Subject and description are required.");
        }
        if (request.getSubject().length() > 255 || request.getDescription().length() > 2000) {
            throw new IllegalArgumentException("Subject or description exceeds the allowed length.");
        }
        Long loanAccountId = request.getLoanAccountId();
        if (loanAccountId != null) {
            LoanAccount account = loanAccountRepository.findById(loanAccountId)
                    .orElseThrow(() -> new NoSuchElementException("Loan account was not found."));
            if (account.getCustomer() == null || !customer.getCustomerId().equals(account.getCustomer().getCustomerId())) {
                throw new IllegalArgumentException("The selected loan account does not belong to the authenticated customer.");
            }
        }

        String attachmentPath = null;
        MultipartFile attachment = request.getAttachment();
        if (attachment != null && !attachment.isEmpty()) {
            attachmentPath = fileStorage.store(attachment, "support", customer.getCustomerId());
        }
        try {
            LocalDateTime now = LocalDateTime.now();
            SupportTicket ticket = new SupportTicket();
            ticket.setCustomerId(customer.getCustomerId());
            ticket.setLoanAccountId(loanAccountId);
            ticket.setSubject(request.getSubject().trim());
            ticket.setDescription(request.getDescription().trim());
            ticket.setStatus("OPEN");
            ticket.setCreatedAt(now);
            ticket.setUpdatedAt(now);
            ticket.setAttachmentPath(attachmentPath);
            return toResponse(ticketRepository.save(ticket));
        } catch (RuntimeException ex) {
            fileStorage.deleteQuietly(attachmentPath);
            throw ex;
        }
    }

    @Override
    public SupportTicketResponseDto getMyTicket(String customerEmail, Long ticketId) {
        Customer customer = requireCustomer(customerEmail);
        SupportTicket ticket = ticketRepository.findByTicketIdAndCustomerId(ticketId, customer.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Support ticket was not found for this customer."));
        return toResponse(ticket);
    }

    @Override
    public List<SupportTicketResponseDto> getMyTickets(String customerEmail) {
        Customer customer = requireCustomer(customerEmail);
        return ticketRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getCustomerId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public Resource getMyTicketAttachment(String customerEmail, Long ticketId) {
        Customer customer = requireCustomer(customerEmail);
        SupportTicket ticket = ticketRepository.findByTicketIdAndCustomerId(ticketId, customer.getCustomerId())
                .orElseThrow(() -> new NoSuchElementException("Support ticket was not found for this customer."));
        return fileStorage.loadAsResource(ticket.getAttachmentPath());
    }

    private Customer requireCustomer(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Customer account was not found."));
    }

    private SupportTicketResponseDto toResponse(SupportTicket ticket) {
        SupportTicketResponseDto dto = new SupportTicketResponseDto();
        dto.setTicketId(ticket.getTicketId()); dto.setCustomerId(ticket.getCustomerId());
        dto.setLoanAccountId(ticket.getLoanAccountId()); dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription()); dto.setStatus(ticket.getStatus());
        dto.setCreatedAt(ticket.getCreatedAt()); dto.setUpdatedAt(ticket.getUpdatedAt());
        if (StringUtils.hasText(ticket.getAttachmentPath())) {
            dto.setAttachmentUrl("/auth/v1/customer/support/tickets/" + ticket.getTicketId() + "/attachment");
        }
        return dto;
    }
}
