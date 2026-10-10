package com.loanapp.loan_application.controller.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;
import com.loanapp.loan_application.service.payment.RazorpayService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/razorpay")
public class RazorpayController {

    private final RazorpayService service;

    public RazorpayController(RazorpayService service) {
        this.service = service;
    }

    @PostMapping("/order")
    public String createOrder(@RequestParam BigDecimal amount, @RequestParam Long loanAccountId, @RequestParam Long emiScheduleId) {
        return service.createOrder(amount, loanAccountId, emiScheduleId);
    }

    @PostMapping("/verify")
    public LoanPaymentsDto verifyPayment(@RequestParam String orderId, @RequestParam String paymentId,
            @RequestParam String signature, @RequestBody LoanPaymentsDto dto) {
        return service.verifyPayment(orderId, paymentId, signature, dto);
    }
}