package com.loanapp.loan_application.controller.supportticket;

import com.loanapp.loan_application.dto.supportticket.SupportTicketRequestDto;
import com.loanapp.loan_application.dto.supportticket.SupportTicketResponseDto;
import com.loanapp.loan_application.service.supportticket.CustomerSupportTicketService;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/auth/v1/customer/support/tickets")
@CrossOrigin("http://localhost:4200/")
public class CustomerSupportTicketController {
    private final CustomerSupportTicketService service;
    public CustomerSupportTicketController(CustomerSupportTicketService service) { this.service = service; }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SupportTicketResponseDto> create(
            @AuthenticationPrincipal String email, @Valid @ModelAttribute SupportTicketRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createTicket(email, request));
    }

    @GetMapping
    public ResponseEntity<List<SupportTicketResponseDto>> myTickets(@AuthenticationPrincipal String email) {
        return ResponseEntity.ok(service.getMyTickets(email));
    }

    @GetMapping("/{ticketId}")
    public ResponseEntity<SupportTicketResponseDto> one(@AuthenticationPrincipal String email, @PathVariable Long ticketId) {
        return ResponseEntity.ok(service.getMyTicket(email, ticketId));
    }

    @GetMapping("/{ticketId}/attachment")
    public ResponseEntity<Resource> attachment(@AuthenticationPrincipal String email, @PathVariable Long ticketId) {
        Resource resource = service.getMyTicketAttachment(email, ticketId);
        MediaType type = MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM);
        ContentDisposition disposition = ContentDisposition.inline()
                .filename(resource.getFilename() == null ? "attachment" : resource.getFilename(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok().contentType(type).header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString()).body(resource);
    }
}
