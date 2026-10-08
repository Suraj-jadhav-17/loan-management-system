package com.loanapp.loan_application.controller.supportticket;

import com.loanapp.loan_application.dto.supportticket.*;
import com.loanapp.loan_application.service.supportticket.SupportTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/support")
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    public SupportTicketController(
            SupportTicketService supportTicketService) {

        this.supportTicketService =
                supportTicketService;
    }

    @PostMapping("/tickets")
    public ResponseEntity<SupportTicketResponseDto>
    createTicket(
            @RequestBody SupportTicketRequestDto request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        supportTicketService.createTicket(
                                request
                        )
                );
    }

    @GetMapping("/tickets/{ticketId}")
    public ResponseEntity<SupportTicketResponseDto>
    getTicket(
            @PathVariable Integer ticketId) {

        return ResponseEntity.ok(
                supportTicketService.getTicket(ticketId)
        );
    }

    @GetMapping("/customer/{customerId}/tickets")
    public ResponseEntity<List<SupportTicketResponseDto>>
    getCustomerTickets(
            @PathVariable Integer customerId) {

        return ResponseEntity.ok(
                supportTicketService
                        .getCustomerTickets(customerId)
        );
    }

    @GetMapping("/loan/{loanAccountId}/tickets")
    public ResponseEntity<List<SupportTicketResponseDto>>
    getLoanTickets(
            @PathVariable Integer loanAccountId) {

        return ResponseEntity.ok(
                supportTicketService
                        .getLoanTickets(loanAccountId)
        );
    }

    @GetMapping("/officer/tickets")
    public ResponseEntity<List<SupportTicketResponseDto>>
    getOfficerTickets(
            @RequestParam(required = false)
            String status) {

        return ResponseEntity.ok(
                supportTicketService
                        .getOfficerTickets(status)
        );
    }

    @PutMapping(
            "/officer/tickets/{ticketId}/status")
    public ResponseEntity<String>
    updateTicketStatus(
            @PathVariable Integer ticketId,
            @RequestBody
            SupportTicketStatusRequestDto request) {

        supportTicketService.updateStatus(
                ticketId,
                request.getStatus()
        );

        return ResponseEntity.ok(
                "Support ticket status updated successfully."
        );
    }
}