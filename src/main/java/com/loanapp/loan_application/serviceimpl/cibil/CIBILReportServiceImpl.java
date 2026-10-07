//package com.loanapp.loan_application.serviceimpl.cibil;
//
//import com.loanapp.loan_application.dto.cibil.CIBILReportRequestDto;
//import com.loanapp.loan_application.dto.cibil.CIBILReportResponseDto;
//import com.loanapp.loan_application.entity.Customer;
//import com.loanapp.loan_application.entity.cibil.CIBILReport;
//import com.loanapp.loan_application.repository.CustomerRepository;
//import com.loanapp.loan_application.repository.cibil.CIBILReportRepo;
//import com.loanapp.loan_application.service.cibil.CIBILReportService;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@Service
//@RequiredArgsConstructor
//public class CIBILReportServiceImpl implements CIBILReportService {
//
//    private final CIBILReportRepo cibilReportRepo;
//    private final CustomerRepository customerRepository;
//    private final ModelMapper modelMapper;
//
//
//    @Override
//    public CIBILReportResponseDto createReport(CIBILReportRequestDto request) {
//
//        Customer customer=customerRepository.findById(request.getCustomerId())
//                .orElseThrow(()-> new RuntimeException("Customer not found"));
//
//        CIBILReport report = CIBILReport.builder()
//                .customer(customer)
//                .panNo(request.getPanNo())
//                .checkDate(LocalDateTime.now())
//                .build();
//
//        CIBILReport savedReport=cibilReportRepo.save(report);
//
//        CIBILReportResponseDto response=modelMapper.map(savedReport, CIBILReportResponseDto.class);
//
//        response.setCustomerId(savedReport.getCustomer().getCustomerId());
//
//        return response;
//    }
//
//    @Override
//    public CIBILReportResponseDto getLatestReport(Long customerId) {
//
//        CIBILReport report=cibilReportRepo.findTopByCustomerIdOrderByCheckDateDesc(customerId)
//                .orElseThrow(()->
//                        new RuntimeException("CIBIL report not found"));
//
//        CIBILReportResponseDto response=modelMapper.map(report,CIBILReportResponseDto.class);
//
//        response.setCustomerId(report.getCustomer().getCustomerId());
//
//        return response;
//    }
//}
