package com.loanapp.loan_application.controller.report;

import com.loanapp.loan_application.dto.report.TotalApplicationsDto;
import com.loanapp.loan_application.response.ApiResponse;
import com.loanapp.loan_application.service.report.ReportService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/auth/v1/loan-officer")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("totalApplication")

    public ResponseEntity<?> getAllApplications(HttpServletRequest request){

        TotalApplicationsDto totalApplicationsDto =  reportService.getAllApplication();
        if(totalApplicationsDto==null){
            throw  new RuntimeException("No any Data Available");
        }
        ApiResponse response =    new ApiResponse<>(
                "200",
                true,
                "All Application Fetch Successfully",
                totalApplicationsDto,
                LocalDateTime.now(),
                request.getRequestURI(),
                null
        );
        return ResponseEntity.ok(response);

    }
}
