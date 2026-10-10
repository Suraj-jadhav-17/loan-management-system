package com.loanapp.loan_application.service.emischeduler;

import com.loanapp.loan_application.dto.emischeduler.EmiSchedulesDto;

import java.math.BigDecimal;
import java.util.List;

public interface EmiSchedulesService {

    List<EmiSchedulesDto> getByLoanId(Long loanAccountId);
    void generate(Long loanAccountId);
    void checkDefault(Long loanAccountId);
    void regenerateAfterPartialForeclosure(Long loanAccountId, BigDecimal newOutstandingPrincipal);

}