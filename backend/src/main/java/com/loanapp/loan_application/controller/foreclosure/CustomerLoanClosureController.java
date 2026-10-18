
package com.loanapp.loan_application.controller.foreclosure;

import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/auth/v1/customer/loan-closure")
@RequiredArgsConstructor
public class CustomerLoanClosureController {

    private final LoanClosureService loanClosureService;
    private final LoanAccountRepository loanAccountRepository;
    private final UserRepository userRepository;

    @GetMapping("/history")
    public List<LoanClosureResponseDto> getCustomerClosureHistory(Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        if (user.getCustomer() == null || user.getCustomer().getCustomerId() == null) {
            throw new RuntimeException("Authenticated user is not linked to a customer");
        }

        return loanClosureService.getCustomerClosureHistory(user.getCustomer().getCustomerId());
    }

    @GetMapping("/{loanAccountId}")
    public LoanClosureResponseDto getClosure(@PathVariable Long loanAccountId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);

        LoanAccount loan = loanAccountRepository.findById(loanAccountId)
                .orElseThrow(() -> new RuntimeException("Loan account not found"));

        if (user.getCustomer() == null
                || user.getCustomer().getCustomerId() == null
                || loan.getCustomer() == null
                || loan.getCustomer().getCustomerId() == null
                || !loan.getCustomer().getCustomerId()
                .equals(user.getCustomer().getCustomerId())) {
            throw new RuntimeException(
                    "You are not allowed to access this loan closure"
            );
        }

        return loanClosureService.getClosure(loanAccountId);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null) {
            throw new RuntimeException("User is not authenticated");
        }

        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
