package com.loanapp.loan_application.dto.foreclosure;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForeClosureApprovalDto {

    private Long foreClosureId;

    private Long officerId;

    private String remarks;
}