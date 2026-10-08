package com.loanapp.loan_application.controller.multiplepayment;

import com.loanapp.loan_application.dto.multiplepayment.MultipleEmiRequestDto;
import com.loanapp.loan_application.service.multiplepayment.MultipleEmiRequestService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/multiple-emi")
public class MultipleEmiRequestController {

    private final MultipleEmiRequestService service;

    public MultipleEmiRequestController(MultipleEmiRequestService service) {
        this.service = service;
    }

    @PostMapping("/request")
    public MultipleEmiRequestDto request(@RequestBody MultipleEmiRequestDto dto) {
        return service.request(dto);
    }

    @GetMapping("/loan/{loanAccountId}")
    public List<MultipleEmiRequestDto> getByLoan(@PathVariable Long loanAccountId) {
        return service.getByLoan(loanAccountId);
    }

    @PutMapping("/approve/{requestId}")
    public MultipleEmiRequestDto approve(@PathVariable Long requestId) {

        return service.approve(requestId);
    }


    @PostMapping("/order/{requestId}")
    public String createOrder(@PathVariable Long requestId) {

        return service.createOrder(requestId);
    }

    @PostMapping("/verify/{requestId}")
    public MultipleEmiRequestDto verifyPayment(@PathVariable Long requestId, @RequestParam String orderId,
                                               @RequestParam String paymentId, @RequestParam String signature) {
        return service.verifyPayment(requestId, orderId, paymentId, signature);
    }
}