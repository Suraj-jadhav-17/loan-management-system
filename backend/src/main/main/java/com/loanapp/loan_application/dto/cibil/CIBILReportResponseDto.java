package com.loanapp.loan_application.dto.cibil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CIBILReportResponseDto {

    private Long id;

    private Long customerId;

    private String panNo;

    private Integer cibilScore;

    private LocalDateTime checkDate;
}