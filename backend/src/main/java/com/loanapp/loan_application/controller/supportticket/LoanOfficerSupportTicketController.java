package com.loanapp.loan_application.controller.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;
import com.loanapp.loan_application.dto.supportticket.SupportTicketStatusRequestDto;
import com.loanapp.loan_application.service.supportticket.LoanOfficerSupportTicketService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth/v1/loan-office/support/tickets")
@CrossOrigin("http://localhost:4200/")
public class LoanOfficerSupportTicketController {
    private final LoanOfficerSupportTicketService service;
    public LoanOfficerSupportTicketController(LoanOfficerSupportTicketService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<SupportTicketResponseDto>> tickets(@RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.getTickets(status));
    }

    @GetMapping("/loan-account/{loanAccountId}")
    public ResponseEntity<List<SupportTicketResponseDto>> ticketsForLoan(@PathVariable Long loanAccountId) {
        return ResponseEntity.ok(service.getTicketsForLoanAccount(loanAccountId));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponseDto> ticket(@PathVariable Long ticketId) {
        return ResponseEntity.ok(service.getTicket(ticketId));
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<Map<String, String>> updateStatus(@PathVariable Long ticketId,
                                                             @Valid @RequestBody SupportTicketStatusRequestDto request) {
        service.updateStatus(ticketId, request.getStatus());
        return ResponseEntity.ok(Map.of("message", "Support ticket status updated."));
    }

    @GetMapping("/{ticketId}/attachment")
    public ResponseEntity<Resource> attachment(@PathVariable Long ticketId) {
        Resource resource = service.getTicketAttachment(ticketId);
        MediaType type = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(resource.getFilename() == null ? "attachment" : resource.getFilename(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString()).body(resource);
    }
}
