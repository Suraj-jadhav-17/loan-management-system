package com.loanapp.loan_application.controller.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;
import com.loanapp.loan_application.service.payment.LoanPaymentsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class LoanPaymentsController {

    private final LoanPaymentsService service;

    public LoanPaymentsController(LoanPaymentsService service) {
        this.service = service;
    }

    @PostMapping
    public LoanPaymentsDto makePayment(@RequestBody LoanPaymentsDto dto) {
        return service.makePayment(dto);
    }

    @GetMapping("/{loanAccountId}")
    public List<LoanPaymentsDto> getPayments(@PathVariable Long loanAccountId) {
        return service.getPayments(loanAccountId);
    }

}
