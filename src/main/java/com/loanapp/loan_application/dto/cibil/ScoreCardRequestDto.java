package com.loanapp.loan_application.dto.cibil;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreCardRequestDto {

    @NotNull
    private Long customerId;
}
