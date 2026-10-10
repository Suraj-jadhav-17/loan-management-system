package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.repository.register.CustomerRepository;
import com.loanapp.loan_application.service.cibil.ScoreCardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/customer/scorecards")
@RequiredArgsConstructor
public class CustomerScoreCardController {

    private final ScoreCardService scoreCardService;
    private final CustomerRepository customerRepository;

    private Long getCustomerId(Authentication authentication) {

        Customer customer = customerRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return customer.getCustomerId();
    }

    @PostMapping
    public ResponseEntity<ScoreCardResponseDto> createScoreCard(Authentication authentication) {

        ScoreCardRequestDto request = new ScoreCardRequestDto();
        request.setCustomerId(getCustomerId(authentication));

        return ResponseEntity.status(HttpStatus.CREATED).body(scoreCardService.createScoreCard(request));
    }

    @GetMapping("/latest")
    public ResponseEntity<ScoreCardResponseDto> getLatestScoreCard(Authentication authentication) {
        return ResponseEntity.ok(scoreCardService.getLatestScoreCard(getCustomerId(authentication)));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<ScoreCardResponseDto>> getScoreCardHistory(Authentication authentication, Pageable pageable) {
        return ResponseEntity.ok(scoreCardService.getScoreCardHistory(getCustomerId(authentication), pageable));
    }
}