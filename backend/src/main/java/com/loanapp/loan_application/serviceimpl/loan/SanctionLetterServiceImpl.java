package com.loanapp.loan_application.serviceimpl.loan;

import com.loanapp.loan_application.entity.loan.LoanDeal;
import com.loanapp.loan_application.entity.loan.SanctionLetter;
import com.loanapp.loan_application.entity.register.Customer;
import com.loanapp.loan_application.exception.ResourceNotFoundException;
import com.loanapp.loan_application.repository.loan.LoanDealRepo;
import com.loanapp.loan_application.repository.loan.SanctionLetterRepo;
import com.loanapp.loan_application.service.loan.SanctionLetterService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SanctionLetterServiceImpl implements SanctionLetterService {

    private final SanctionLetterRepo letterRepo;
    private final LoanDealRepo dealRepo;
    private final SanctionPDFGenerator pdfGenerator;

    @Override
    @Transactional
    public SanctionLetter createSanctionLetter(Long loanDealId) {
        LoanDeal deal = dealRepo.findById(loanDealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));

        return letterRepo.findByLoanDeal_Id(loanDealId)
                .orElseGet(() -> {
                    SanctionLetter letter = SanctionLetter.builder()
                            .applicationNo(generateApplicationNumber())
                            .createdAt(LocalDateTime.now())
                            .emiAmount(deal.getEmiAmount())
                            .interestRate(deal.getInterestRate())
                            .loanAmount(deal.getAmount())
                            .tenureMonth(deal.getTenureMonths())
                            .loanDeal(deal)
                            .build();

                    return letterRepo.save(letter);
                });
    }

    private String generateApplicationNumber() {
        return "APP" + java.util.UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 10)
                .toUpperCase();
    }

    @Override
    public SanctionLetter getSanctionLetterById(Long id) {
        return letterRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sanction letter not found"));
    }

    @Override
    public byte[] downloadSanctionLetter(Long loanDealId) {
        return generateSanctionLetterPdf(loanDealId);
    }

    @Override
    public byte[] viewSanctionLetter(Long loanDealId) {
        return generateSanctionLetterPdf(loanDealId);
    }

    private byte[] generateSanctionLetterPdf(Long loanDealId) {
        SanctionLetter letter = letterRepo.findByLoanDeal_Id(loanDealId)
                .orElseThrow(() -> new ResourceNotFoundException("Sanction letter not found"));

        LoanDeal deal = dealRepo.findById(loanDealId)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));

        Customer customer = deal.getCustomer();

        String name = customer.getFirstName() + " " + customer.getLastName();

        try {
            return pdfGenerator.generatePdf(
                    letter,
                    name,
                    customer.getMobileNo(),
                    deal.getLoanType()
            );
        } catch (IOException e) {
            throw new IllegalStateException("Failed to generate sanction letter PDF", e);
        }
    }
}