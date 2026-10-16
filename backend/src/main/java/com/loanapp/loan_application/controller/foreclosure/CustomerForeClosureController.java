package com.loanapp.loan_application.controller.foreclosure;

import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureCreateRequestDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureRequest;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureStatus;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.foreclosure.ForeClosureRequestRepo;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.foreclosure.ForeClosureService;
import com.loanapp.loan_application.service.payment.RazorpayService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/v1/customer/foreclosure")
@RequiredArgsConstructor
public class CustomerForeClosureController {
    private final ForeClosureService foreClosureService;
    private final LoanAccountRepository loanAccountRepository;
    private final ForeClosureRequestRepo foreClosureRequestRepo;
    private final UserRepository userRepository;
    private final RazorpayService razorpayService;

    @PostMapping("/requests")
    public ForeClosureResponseDto createRequest(
            @RequestBody ForeClosureCreateRequestDto request,
            Authentication authentication) {
        User user = getUser(authentication);
        LoanAccount loan = getLoan(request.getLoanAccountId());
        verifyOwnership(user, loan);
        return foreClosureService.createRequest(request);
    }

    @GetMapping("/requests/{foreClosureId}")
    public ForeClosureResponseDto getRequest(
            @PathVariable Long foreClosureId,
            Authentication authentication) {
        User user = getUser(authentication);
        ForeClosureRequest request = getRequestEntity(foreClosureId);
        verifyOwnership(user, request.getLoanAccount());
        return foreClosureService.getRequest(foreClosureId);
    }

    @PostMapping("/requests/{foreClosureId}/payment-order")
    public String createPaymentOrder(
            @PathVariable Long foreClosureId,
            Authentication authentication) {
        User user = getUser(authentication);
        ForeClosureRequest foreclosure = getRequestEntity(foreClosureId);
        verifyOwnership(user, foreclosure.getLoanAccount());

        if (foreclosure.getStatus() != ForeClosureStatus.APPROVED
                || Boolean.TRUE.equals(foreclosure.getIsPaid())) {
            throw new RuntimeException("Only an approved unpaid foreclosure request can be paid");
        }

        return razorpayService.createOrder(
                foreclosure.getForeClosureAmount(),
                foreclosure.getLoanAccount().getLoanAccountId(),
                foreClosureId);
    }

    @PostMapping("/payments")
    public ForeClosureResponseDto makePayment(
            @RequestBody ForeClosurePaymentDto request,
            Authentication authentication) {
        if (request == null || request.getForeClosureId() == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }
        User user = getUser(authentication);
        ForeClosureRequest foreclosure = getRequestEntity(request.getForeClosureId());
        verifyOwnership(user, foreclosure.getLoanAccount());
        return foreClosureService.makePayment(request);
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    private LoanAccount getLoan(Long loanAccountId) {
        if (loanAccountId == null) {
            throw new RuntimeException("Loan account ID is required");
        }
        return loanAccountRepository.findById(loanAccountId)
                .orElseThrow(() -> new RuntimeException("Loan account not found"));
    }

    private ForeClosureRequest getRequestEntity(Long id) {
        if (id == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }
        return foreClosureRequestRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Foreclosure request not found"));
    }

    private void verifyOwnership(User user, LoanAccount loan) {
        if (user.getCustomer() == null
                || user.getCustomer().getCustomerId() == null
                || loan.getCustomer() == null
                || loan.getCustomer().getCustomerId() == null
                || !loan.getCustomer().getCustomerId()
                .equals(user.getCustomer().getCustomerId())) {
            throw new RuntimeException("You are not allowed to access this loan account");
        }
    }
}