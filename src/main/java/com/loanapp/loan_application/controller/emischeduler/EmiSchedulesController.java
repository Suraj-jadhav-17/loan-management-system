package com.loanapp.loan_application.controller.emischeduler;

import com.loanapp.loan_application.dto.emischeduler.EmiSchedulesDto;
import com.loanapp.loan_application.service.emischeduler.EmiSchedulesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emi")
public class EmiSchedulesController {

    private final EmiSchedulesService service;

    public EmiSchedulesController(EmiSchedulesService service) {
        this.service = service;
    }

    @GetMapping("/{loanAccountId}")   //emi101
    public List<EmiSchedulesDto> getByLoanId(
            @PathVariable Long loanAccountId) {

        return service.getByLoanId(loanAccountId);
    }
    @PostMapping("/generate/{loanAccountId}")
    public String generate(@PathVariable Long loanAccountId) {
        service.generate(loanAccountId);
        return "EMI schedule generated";
    }
}