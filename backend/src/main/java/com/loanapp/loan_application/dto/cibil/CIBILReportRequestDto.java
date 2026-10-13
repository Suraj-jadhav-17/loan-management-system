package com.loanapp.loan_application.dto.cibil;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CIBILReportRequestDto {

    @NotNull
    private Long customerId;

    private String panNo;

    @NotNull
    @Min(300)
    @Max(900)
    private Integer cibilScore;
}
