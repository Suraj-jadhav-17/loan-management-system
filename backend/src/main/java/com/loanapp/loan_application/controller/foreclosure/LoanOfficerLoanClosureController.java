
package com.loanapp.loan_application.controller.foreclosure;

import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/loan-office")
@RequiredArgsConstructor
public class LoanOfficerLoanClosureController {

    private final LoanClosureService loanClosureService;
    private final UserRepository userRepository;

    @PostMapping("/{loanAccountId}/normal")
    public LoanClosureResponseDto closeLoanNormally(@PathVariable Long loanAccountId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return loanClosureService.closeLoanNormally(loanAccountId, user.getUserId());
    }

    @PostMapping("/{loanAccountId}/foreclosure")
    public LoanClosureResponseDto closeLoanByForeclosure(@PathVariable Long loanAccountId, Authentication authentication) {
        User user = getAuthenticatedUser(authentication);
        return loanClosureService.closeLoanByForeclosure(loanAccountId, user.getUserId());
    }

    @GetMapping("/loan-closure/{loanAccountId}")
    public LoanClosureResponseDto getClosure(@PathVariable Long loanAccountId) {
        return loanClosureService.getClosure(loanAccountId);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }
}
