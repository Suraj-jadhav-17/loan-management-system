package com.loanapp.loan_application.dto.cibil;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EligibilityResultRequestDto {

    @NotNull
    private Long customerId;
}
