package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScoreCardService {

    ScoreCardResponseDto createScoreCard(ScoreCardRequestDto request);

    ScoreCardResponseDto getLatestScoreCard(Long customerId);

    Page<ScoreCardResponseDto> getScoreCardHistory(Long customerId, Pageable pageable);
}
