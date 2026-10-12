package com.loanapp.loan_application.controller.foreclosure;

import com.loanapp.loan_application.dto.foreclosure.ForeClosureApprovalDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;
import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.foreclosure.ForeClosureService;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth/v1/loan-office")
@RequiredArgsConstructor
public class LoanOfficerForeClosureController {

    private final ForeClosureService foreClosureService;
    private final LoanClosureService loanClosureService;
    private final UserRepository userRepository;

    @GetMapping("/requests/pending")
    public List<ForeClosureResponseDto> getPendingRequests() {
        return foreClosureService.getPendingRequests();
    }

    @GetMapping("/requests/{foreClosureId}")
    public ForeClosureResponseDto getRequest(@PathVariable Long foreClosureId) {
        return foreClosureService.getRequest(foreClosureId);
    }

    @PostMapping("/requests/{foreClosureId}/approve")
    public ForeClosureResponseDto approve(@PathVariable Long foreClosureId, @RequestBody(required = false) ForeClosureApprovalDto body,
            Authentication authentication) {
        ForeClosureApprovalDto request = buildApproval(foreClosureId, body, authentication);
        return foreClosureService.approveRequest(request);
    }

    @PostMapping("/requests/{foreClosureId}/reject")
    public ForeClosureResponseDto reject(@PathVariable Long foreClosureId, @RequestBody(required = false) ForeClosureApprovalDto body,
            Authentication authentication) {
        ForeClosureApprovalDto request = buildApproval(foreClosureId, body, authentication);
        return foreClosureService.rejectRequest(request);
    }

    @PostMapping("/closures/{loanAccountId}/normal")
    public LoanClosureResponseDto closeNormally(@PathVariable Long loanAccountId, Authentication authentication) {
        User officer = getUser(authentication);
        return loanClosureService.closeLoanNormally(loanAccountId, officer.getUserId());
    }

    @GetMapping("/closures/{loanAccountId}")
    public LoanClosureResponseDto getClosure(@PathVariable Long loanAccountId) {
        return loanClosureService.getClosure(loanAccountId);
    }

    private ForeClosureApprovalDto buildApproval(Long id, ForeClosureApprovalDto body, Authentication authentication) {
        User officer = getUser(authentication);
        return ForeClosureApprovalDto.builder()
                .foreClosureId(id)
                .officerId(officer.getUserId())
                .remarks(body == null ? null : body.getRemarks())
                .build();
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Authenticated officer not found"));
    }
}
