
package com.loanapp.loan_application.controller.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;
import com.loanapp.loan_application.service.cibil.ScoreCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth/v1/loan-office/scorecards")
@RequiredArgsConstructor
public class LoanOfficerScoreCardController {

    private final ScoreCardService scoreCardService;

    @PostMapping("/{customerId}")
    public ResponseEntity<ScoreCardResponseDto> createScoreCard(@PathVariable Long customerId) {

        ScoreCardRequestDto request = new ScoreCardRequestDto();
        request.setCustomerId(customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(scoreCardService.createScoreCard(request));
    }

    @GetMapping("/{customerId}/latest")
    public ResponseEntity<ScoreCardResponseDto> getLatestScoreCard(@PathVariable Long customerId) {

        return ResponseEntity.ok(scoreCardService.getLatestScoreCard(customerId));
    }

    @GetMapping("/{customerId}/history")
    public ResponseEntity<Page<ScoreCardResponseDto>> getScoreCardHistory(@PathVariable Long customerId, Pageable pageable) {

        return ResponseEntity.ok(scoreCardService.getScoreCardHistory(customerId, pageable));
    }
}
