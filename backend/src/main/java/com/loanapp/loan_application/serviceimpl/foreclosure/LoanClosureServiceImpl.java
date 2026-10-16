package com.loanapp.loan_application.serviceimpl.foreclosure;

import com.loanapp.loan_application.dto.foreclosure.LoanClosureResponseDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureRequest;
import com.loanapp.loan_application.entity.foreclosure.LoanClosure;
import com.loanapp.loan_application.entity.foreclosure.LoanClosureStatus;
import com.loanapp.loan_application.entity.foreclosure.LoanClosureType;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.foreclosure.ForeClosureRequestRepo;
import com.loanapp.loan_application.repository.foreclosure.LoanClosureRepo;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import com.loanapp.loan_application.service.mail.EmailService;

@Service
@RequiredArgsConstructor
@Transactional
public class LoanClosureServiceImpl implements LoanClosureService {
    private final LoanClosureRepo loanClosureRepo;
    private final LoanAccountRepository loanAccountRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final ForeClosureRequestRepo foreClosureRequestRepo;
    private final ModelMapper modelMapper;
    private final EmailService emailService;



    @Override
    public LoanClosureResponseDto closeLoanNormally(Long loanAccountId, Long closedBy) {
        LoanAccount loan = getLoan(loanAccountId);

        if (loanClosureRepo.existsByLoanAccountLoanAccountId(loanAccountId)) {
            throw new RuntimeException("Loan closure record already exists");
        }

        List<EmiSchedules> schedules = emiScheduleRepository.findByLoanAccountId(loanAccountId);

        if (schedules.isEmpty() || schedules.stream()
                .anyMatch(e -> e.getPaymentStatus() != PaymentStatus.PAID)) {
            throw new RuntimeException("All EMIs must be paid before normal closure");
        }

        if (loan.getOutstandingPrincipal() == null
                || loan.getOutstandingPrincipal().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("Outstanding principal must be exactly zero before closure");
        }

        loan.setOutstandingPrincipal(BigDecimal.ZERO);
        loan.setLoanStatus(LoanStatus.CLOSED);
        loanAccountRepository.save(loan);

        LoanClosure closure = LoanClosure.builder()
                .loanAccount(loan)
                .closureType(LoanClosureType.NORMAL)
                .finalSettlementAmount(BigDecimal.ZERO)
                .closureDate(LocalDateTime.now())
                .closureStatus(LoanClosureStatus.CLOSED)
                .closedBy(closedBy)
                .nocGenerated(false)
                .cibilUpdated(false)
                .build();

        LoanClosure savedClosure = loanClosureRepo.save(closure);

        emailService.sendLoanNotification(
                loan.getCustomer().getEmail(),
                "Loan Closure Completed",
                "Dear " + loan.getCustomer().getFirstName() + ",\n\n"
                        + "Congratulations! Your loan has been closed successfully.\n"
                        + "Loan Account: " + loan.getLoanAccountNo() + "\n"
                        + "Closure Type: Normal Closure\n"
                        + "Closure Date: " + savedClosure.getClosureDate() + "\n"
                        + "Outstanding Principal: " + loan.getOutstandingPrincipal() + "\n"
                        + "Status: CLOSED\n\n"
                        + "Thank you for banking with us."
        );

        return toDto(savedClosure);
    }


    @Override
    public LoanClosureResponseDto closeLoanByForeclosure(Long loanAccountId, Long closedBy) {
        LoanAccount loan = getLoan(loanAccountId);

        if (loanClosureRepo.existsByLoanAccountLoanAccountId(loanAccountId)) {
            throw new RuntimeException("Loan closure record already exists");
        }

        if (!"CLOSED".equalsIgnoreCase(String.valueOf(loan.getLoanStatus()))
                || loan.getOutstandingPrincipal() == null
                || loan.getOutstandingPrincipal().compareTo(BigDecimal.ZERO) != 0) {
            throw new RuntimeException("Loan must be fully settled before closure");
        }

        ForeClosureRequest request = foreClosureRequestRepo
                .findTopByLoanAccountLoanAccountIdOrderByForeClosureIdDesc(loanAccountId)
                .orElseThrow(() -> new RuntimeException("Foreclosure request not found"));

        LoanClosure closure = LoanClosure.builder()
                .loanAccount(loan)
                .closureType(LoanClosureType.FORECLOSURE)
                .finalSettlementAmount(request.getForeClosureAmount())
                .closureDate(LocalDateTime.now())
                .closureStatus(LoanClosureStatus.CLOSED)
                .closedBy(closedBy)
                .nocGenerated(false)
                .cibilUpdated(false)
                .build();

        return toDto(loanClosureRepo.save(closure));
    }

    @Override
    @Transactional(readOnly = true)
    public LoanClosureResponseDto getClosure(Long loanAccountId) {
        LoanClosure closure = loanClosureRepo.findByLoanAccountLoanAccountId(loanAccountId)
                .orElseThrow(() -> new RuntimeException("Loan closure record not found"));
        return toDto(closure);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LoanClosureResponseDto> getClosureHistory() {
        return loanClosureRepo.findAllByOrderByClosureDateDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private LoanAccount getLoan(Long loanAccountId) {
        return loanAccountRepository.findById(loanAccountId)
                .orElseThrow(() -> new RuntimeException("Loan account not found"));
    }

    private LoanClosureResponseDto toDto(LoanClosure closure) {
        LoanClosureResponseDto dto = modelMapper.map(closure, LoanClosureResponseDto.class);
        dto.setLoanAccountId(closure.getLoanAccount().getLoanAccountId());
        return dto;
    }
}