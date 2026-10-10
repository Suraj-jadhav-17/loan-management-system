package com.loanapp.loan_application.controller.emischeduler;

import com.loanapp.loan_application.dto.emischeduler.EmiSchedulesDto;
import com.loanapp.loan_application.service.emischeduler.EmiSchedulesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/emi")

public class EmiSchedulesCon {
    private final EmiSchedulesService emiSchedulesService;

    public EmiSchedulesCon(EmiSchedulesService emiSchedulesService) {
        this.emiSchedulesService = emiSchedulesService;
    }
    @GetMapping("/{loanAccountId]")
    public List<EmiSchedulesDto>getByLoanId(@PathVariable Long loanAccountId) {
        return emiSchedulesService.getByLoanId(loanAccountId);
    }
       @PostMapping("/generate/{loanAccountId]")
       public String generate(@PathVariable Long loanAccountId){
           emiSchedulesService.generate(loanAccountId);
           return"Emi Schedule generated";
        }
    }


