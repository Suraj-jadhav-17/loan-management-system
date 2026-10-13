package com.loanapp.loan_application.dto.loan;

import com.loanapp.loan_application.entity.loan.LoanStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanAccountResponse {
    private Long loanAccountId;
    private String loanAccountNo;
   private Long customerId;
   private Long dealId;
    private BigDecimal loanAmount;
  private  BigDecimal outstandingPrincipal;
   private  BigDecimal totalPaidAmount;
   private BigDecimal interestRate;
  private  Integer tenureMonths;
   private BigDecimal emiAmount;
   private LoanStatus loanStatus;
   private  LocalDateTime disbursementDate;
    private LocalDateTime createdAt;
}
