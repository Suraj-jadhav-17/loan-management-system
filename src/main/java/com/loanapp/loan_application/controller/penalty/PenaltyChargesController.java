package com.loanapp.loan_application.controller.penalty;

import com.loanapp.loan_application.dto.penalty.PenaltyChargesDto;
import com.loanapp.loan_application.service.penalty.PenaltyChargesService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/penalties")
public class PenaltyChargesController {

    private final PenaltyChargesService service;

    public PenaltyChargesController(PenaltyChargesService service) {

        this.service = service;
    }

    @PostMapping
    public PenaltyChargesDto add(@RequestBody PenaltyChargesDto dto) {

        return service.add(dto);
    }

    @GetMapping("/loan/{loanAccountId}")
    public List<PenaltyChargesDto> getByLoan(@PathVariable Long loanAccountId) {
        return service.getByLoan(loanAccountId);
    }

    @GetMapping("/emi/{emiScheduleId}")
    public List<PenaltyChargesDto> getByEmi(@PathVariable Long emiScheduleId) {
        return service.getByEmi(emiScheduleId);
    }

    @PostMapping("/overdue")
    public String addOverdue() {
        service.addOverdue();
        return "Overdue penalties added";
    }
}