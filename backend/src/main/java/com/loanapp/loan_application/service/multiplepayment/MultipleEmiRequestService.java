package com.loanapp.loan_application.service.multiplepayment;

import com.loanapp.loan_application.dto.multiplepayment.MultipleEmiRequestDto;

import java.util.List;

public interface MultipleEmiRequestService {

    MultipleEmiRequestDto request(MultipleEmiRequestDto dto);
    List<MultipleEmiRequestDto> getByLoan(Long loanAccountId);
    MultipleEmiRequestDto approve(Long requestId);
    String createOrder(Long requestId);
    MultipleEmiRequestDto verifyPayment(Long requestId, String orderId, String paymentId, String signature
    );
}