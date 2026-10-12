package com.loanapp.loan_application.serviceimpl.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.service.payment.LoanPaymentsService;
import com.loanapp.loan_application.service.payment.RazorpayService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanPaymentsServiceImpl implements LoanPaymentsService {
    private final LoanPaymentsRepository repo;
    private final EmiScheduleRepository emiRepo;
    private final LoanAccountRepository loanRepo;
    public LoanPaymentsServiceImpl(
            LoanPaymentsRepository repo,
            EmiScheduleRepository emiRepo, RazorpayService razorpayService,  LoanAccountRepository loanRepo) {
        this.loanRepo = loanRepo;
        this.repo = repo;
        this.emiRepo = emiRepo;

    }

    @Override
    public LoanPaymentsDto makePayment(LoanPaymentsDto dto) {
        LoanAccount loan = loanRepo.findById(dto.getLoanAccountId())
                .orElseThrow(() ->
                        new RuntimeException("Loan account not found"));

        if ("CLOSED".equalsIgnoreCase(loan.getLoanStatus())) {
            throw new RuntimeException(
                    "Loan account is closed. Please contact Loan Officer.");
        }

        LoanPayments payment = new LoanPayments();

        payment.setLoanAccountId(dto.getLoanAccountId());
        payment.setPaymentAmount(dto.getPaymentAmount());

        payment.setPaymentStatus(dto.getPaymentStatus());
        payment.setPaymentName(dto.getPaymentName());

        LoanPayments saved = repo.save(payment);

        if (saved.getPaymentStatus() == PaymentStatus.PAID) {
            EmiSchedules emi = emiRepo.findById(dto.getEmiScheduleId()).orElseThrow(() ->
                    new RuntimeException("EMI not found"));
            emi.setPaymentStatus(PaymentStatus.PAID);
            emi.setPaidDate(LocalDateTime.now());

            emiRepo.save(emi);
        }

        LoanPaymentsDto result = new LoanPaymentsDto();
        result.setPaymentId(saved.getPaymentId());
        result.setLoanAccountId(saved.getLoanAccountId());
        result.setEmiScheduleId(dto.getEmiScheduleId());
        result.setPaymentAmount(saved.getPaymentAmount());

        result.setPaymentStatus(saved.getPaymentStatus());
        result.setPaymentName(saved.getPaymentName());

        return result;
    }

    @Override
    public List<LoanPaymentsDto> getPayments(Long loanAccountId) {
        List<LoanPayments> payments = repo.findByLoanAccountId(loanAccountId);
        return payments.stream()
                .map(payment -> {
                    LoanPaymentsDto dto = new LoanPaymentsDto();
                    dto.setPaymentId(payment.getPaymentId());
                    dto.setLoanAccountId(payment.getLoanAccountId());
                    dto.setPaymentAmount(payment.getPaymentAmount());
                    dto.setPaymentStatus(payment.getPaymentStatus());
                    dto.setPaymentName(payment.getPaymentName());
                    return dto;
                })
                .collect(Collectors.toList());
    }



}