package com.loanapp.loan_application.dto.cibil;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CIBILReportRequestDto {

    private Long customerId;

    private String panNo;
}