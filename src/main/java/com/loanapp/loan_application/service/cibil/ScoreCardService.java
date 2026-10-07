package com.loanapp.loan_application.service.cibil;

import com.loanapp.loan_application.dto.cibil.ScoreCardRequestDto;
import com.loanapp.loan_application.dto.cibil.ScoreCardResponseDto;

public interface ScoreCardService {

    ScoreCardResponseDto createScoreCard(ScoreCardRequestDto request);

    ScoreCardResponseDto getLatestScoreCard(Long customerId);
}
