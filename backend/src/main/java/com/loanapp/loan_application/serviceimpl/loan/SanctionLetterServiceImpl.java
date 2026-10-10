package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.entity.loan.DealReview;
import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.SanctionLetter;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.loan.DealReviewRepository;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.loan.SanctionLetterRepo;
import com.loanapp.loan_application.service.loan.SanctionLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SanctionLetterServiceImpl implements SanctionLetterService {

    private long applicationCounter = 1;

    private synchronized String generateApplicationNumber() {
        return String.format("APP%03d", applicationCounter++);
    }
    private final SanctionLetterRepo letterRepo;
    private final LoanDealRepo dealRepo;

    private final SanctionPDFGenerator pdfGenerator;


    @Override
    public SanctionLetter createSanctionLetter(Long loanDealId) {
        LoanDeal deal = dealRepo.findById(loanDealId).orElseThrow(()->new ResourceNotFoundException("Deal not found"));

        return letterRepo.save( SanctionLetter.builder()
                .applicationNo(generateApplicationNumber())
                .createdAt(LocalDateTime.now())
                .emiAmount(deal.getEmiAmount())
                .interestRate(deal.getInterestRate())
                .loanAmount(deal.getAmount())
                .tenureMonth(deal.getTenureMonths())
                .loanDeal(deal)
                .build());
    }

    @Override
    public SanctionLetter getSanctionLetterById(Long id) {
        return letterRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("SanctionLetter not Created"));
    }

    @Override
    public byte[] downloadSanctionLetter(Long id) {
       SanctionLetter letter= letterRepo.findByLoanDeal_Id(id).orElseThrow(()->new ResourceNotFoundException("SanctionLetter not Created"));
       LoanDeal deal = dealRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Deal not found"));
        Customer  customer = deal.getCustomer();
       String name = customer.getFirstName()+" "+customer.getLastName();
        try {
            return pdfGenerator.generatePdf(letter,name,customer.getMobileNo(),deal.getLoanType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] viewSanctionLetter(Long id) {
        SanctionLetter letter= letterRepo.findByLoanDeal_Id(id).orElseThrow(()->new ResourceNotFoundException("SanctionLetter not Created"));
        LoanDeal deal = dealRepo.findById(id).orElseThrow(()->new ResourceNotFoundException("Deal not found"));
        Customer  customer = deal.getCustomer();
        String name = customer.getFirstName()+" "+customer.getLastName();
        try {
            return pdfGenerator.generatePdf(letter,name,customer.getMobileNo(),deal.getLoanType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
