package com.loanapp.loan_application.serviceimpl.foreclosure;

import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureApprovalDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureCreateRequestDto;
import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureRequest;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureStatus;
import com.loanapp.loan_application.entity.foreclosure.ForeClosureType;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.foreclosure.ForeClosureRequestRepo;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.foreclosure.ForeClosureService;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ForeClosureServiceImpl implements ForeClosureService {

    private final ForeClosureRequestRepo foreClosureRequestRepo;
    private final LoanAccountRepository loanAccountRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final LoanPaymentsRepository loanPaymentsRepository;
    private final UserRepository userRepository;
    private final LoanClosureService loanClosureService;
    private final ModelMapper modelMapper;

    @Override
    public ForeClosureResponseDto createRequest(ForeClosureCreateRequestDto request) {
        if (request == null || request.getLoanAccountId() == null) {
            throw new RuntimeException("Loan Account ID is required");
        }
        if (request.getForeClosureType() == null) {
            throw new RuntimeException("Foreclosure type is required");
        }

        LoanAccount loanAccount = loanAccountRepository.findById(request.getLoanAccountId())
                .orElseThrow(() -> new RuntimeException("Loan account not found"));

        if (!"ACTIVE".equalsIgnoreCase(loanAccount.getLoanStatus())) {
            throw new RuntimeException("Foreclosure is allowed only for active loan");
        }

        BigDecimal outstandingPrincipal = loanAccount.getOutstandingPrincipal();

        if (outstandingPrincipal == null || outstandingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Loan has no outstanding principal");
        }

        boolean pendingExists = foreClosureRequestRepo
                .existsByLoanAccountLoanAccountIdAndStatus(
                        request.getLoanAccountId(),
                        ForeClosureStatus.PENDING);

        boolean approvedExists = foreClosureRequestRepo
                .existsByLoanAccountLoanAccountIdAndStatus(
                        request.getLoanAccountId(),
                        ForeClosureStatus.APPROVED);

        if (pendingExists || approvedExists) {
            throw new RuntimeException("An active foreclosure request already exists");
        }

        BigDecimal foreclosureAmount;
        BigDecimal partialAmount = null;

        if (request.getForeClosureType() == ForeClosureType.FULL) {
            foreclosureAmount = calculateFullForeclosureAmount(loanAccount);
        } else {
            partialAmount = request.getPartialAmount();

            if (partialAmount == null || partialAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Partial foreclosure amount must be greater than zero");
            }

            if (partialAmount.compareTo(outstandingPrincipal) >= 0) {
                throw new RuntimeException("Partial amount must be less than outstanding principal");
            }

            foreclosureAmount = partialAmount;
        }

        ForeClosureRequest entity = ForeClosureRequest.builder()
                .loanAccount(loanAccount)
                .foreClosureType(request.getForeClosureType())
                .foreClosureAmount(foreclosureAmount)
                .partialAmount(partialAmount)
                .status(ForeClosureStatus.PENDING)
                .isPaid(false)
                .requestedAt(LocalDateTime.now())
                .build();

        return mapToResponse(foreClosureRequestRepo.save(entity));
    }

    @Override
    public ForeClosureResponseDto approveRequest(ForeClosureApprovalDto request) {
        validateOfficer(request.getOfficerId());

        ForeClosureRequest foreclosure = getForeclosureEntity(request.getForeClosureId());

        if (foreclosure.getStatus() != ForeClosureStatus.PENDING) {
            throw new RuntimeException("Only pending foreclosure request can be approved");
        }

        foreclosure.setStatus(ForeClosureStatus.APPROVED);
        foreclosure.setClosedBy(request.getOfficerId());
        foreclosure.setApprovedAt(LocalDateTime.now());
        foreclosure.setRemarks(request.getRemarks());

        return mapToResponse(foreClosureRequestRepo.save(foreclosure));
    }

    @Override
    public ForeClosureResponseDto rejectRequest(ForeClosureApprovalDto request) {
        validateOfficer(request.getOfficerId());

        ForeClosureRequest foreclosure = getForeclosureEntity(request.getForeClosureId());

        if (foreclosure.getStatus() != ForeClosureStatus.PENDING) {
            throw new RuntimeException("Only pending foreclosure request can be rejected");
        }

        foreclosure.setStatus(ForeClosureStatus.REJECTED);
        foreclosure.setClosedBy(request.getOfficerId());
        foreclosure.setRemarks(request.getRemarks());

        return mapToResponse(foreClosureRequestRepo.save(foreclosure));
    }

    @Override
    public ForeClosureResponseDto makePayment(ForeClosurePaymentDto request) {
        ForeClosureRequest foreclosure = getForeclosureEntity(request.getForeClosureId());

        if (foreclosure.getStatus() != ForeClosureStatus.APPROVED) {
            throw new RuntimeException("Foreclosure request must be approved before payment");
        }

        if (Boolean.TRUE.equals(foreclosure.getIsPaid())) {
            throw new RuntimeException("Foreclosure payment is already completed");
        }

        BigDecimal requiredAmount = foreclosure.getForeClosureAmount();
        BigDecimal paidAmount = request.getPaidAmount();

        if (paidAmount == null || paidAmount.compareTo(requiredAmount) != 0) {
            throw new RuntimeException("Paid amount must exactly match foreclosure amount");
        }

        LoanAccount loanAccount = foreclosure.getLoanAccount();

        savePaymentRecord(
                loanAccount.getLoanAccountId(),
                paidAmount,
                foreclosure.getForeClosureType());

        if (foreclosure.getForeClosureType() == ForeClosureType.FULL) {
            processFullForeclosure(foreclosure, loanAccount);
        } else {
            processPartialForeclosure(loanAccount, paidAmount);
        }

        foreclosure.setIsPaid(true);
        foreclosure.setStatus(ForeClosureStatus.PAID);
        foreclosure.setPaymentDate(LocalDateTime.now());

        return mapToResponse(foreClosureRequestRepo.save(foreclosure));
    }

    private void processFullForeclosure(
            ForeClosureRequest foreclosure,
            LoanAccount loanAccount) {

        BigDecimal payment = foreclosure.getForeClosureAmount();

        loanAccount.setOutstandingPrincipal(BigDecimal.ZERO);

        BigDecimal totalPaid = loanAccount.getTotalPaidAmount();

        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        loanAccount.setTotalPaidAmount(totalPaid.add(payment));

        cancelPendingEmis(
                loanAccount.getLoanAccountId(),
                "Loan fully foreclosed");

        loanAccount.setLoanStatus("CLOSED");

        loanAccountRepository.save(loanAccount);

        loanClosureService.closeLoanByForeclosure(
                loanAccount.getLoanAccountId(),
                foreclosure.getClosedBy());
    }

    private void processPartialForeclosure(
            LoanAccount loanAccount,
            BigDecimal partialAmount) {

        BigDecimal oldPrincipal = loanAccount.getOutstandingPrincipal();
        BigDecimal newPrincipal = oldPrincipal.subtract(partialAmount);

        if (newPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Partial foreclosure cannot make balance zero");
        }

        loanAccount.setOutstandingPrincipal(newPrincipal);

        BigDecimal totalPaid = loanAccount.getTotalPaidAmount();

        if (totalPaid == null) {
            totalPaid = BigDecimal.ZERO;
        }

        loanAccount.setTotalPaidAmount(totalPaid.add(partialAmount));
        loanAccount.setLoanStatus("ACTIVE");

        loanAccountRepository.save(loanAccount);
    }

    private void cancelPendingEmis(Long loanAccountId, String reason) {
        List<EmiSchedules> schedules = emiScheduleRepository
                .findByLoanAccountIdAndPaymentStatus(
                        loanAccountId,
                        PaymentStatus.PENDING);

        for (EmiSchedules schedule : schedules) {
            schedule.setPaymentStatus(PaymentStatus.CANCELLED);
            schedule.setCancellationReason(reason);
        }

        emiScheduleRepository.saveAll(schedules);
    }

    private void savePaymentRecord(
            Long loanAccountId,
            BigDecimal amount,
            ForeClosureType type) {

        LoanPayments payment = new LoanPayments();
        payment.setLoanAccountId(loanAccountId);
        payment.setPaymentAmount(amount);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentName(
                type == ForeClosureType.FULL
                        ? "FULL_FORECLOSURE"
                        : "PARTIAL_FORECLOSURE");

        loanPaymentsRepository.save(payment);
    }

    private void validateOfficer(Long officerId) {
        if (officerId == null) {
            throw new RuntimeException("Officer ID is required");
        }

        User user = userRepository.findById(officerId)
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        if (user.getRole() == null || user.getRole().getRoleName() == null) {
            throw new RuntimeException("User role not found");
        }

        String role = user.getRole().getRoleName();

        if (!"ADMIN".equalsIgnoreCase(role)
                && !"LOAN_OFFICER".equalsIgnoreCase(role)
                && !"LOAN OFFICER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Admin or Loan Officer can approve foreclosure");
        }
    }

    private BigDecimal calculateFullForeclosureAmount(LoanAccount loanAccount) {
        BigDecimal principal = loanAccount.getOutstandingPrincipal();
        return principal == null ? BigDecimal.ZERO : principal;
    }

    @Override
    @Transactional(readOnly = true)
    public ForeClosureResponseDto getRequest(Long foreClosureId) {
        return mapToResponse(getForeclosureEntity(foreClosureId));
    }

    private ForeClosureRequest getForeclosureEntity(Long foreClosureId) {
        if (foreClosureId == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }

        return foreClosureRequestRepo.findById(foreClosureId)
                .orElseThrow(() -> new RuntimeException("Foreclosure request not found"));
    }

    private ForeClosureResponseDto mapToResponse(ForeClosureRequest entity) {
        ForeClosureResponseDto response = modelMapper.map(entity, ForeClosureResponseDto.class);
        response.setLoanAccountId(entity.getLoanAccount().getLoanAccountId());
        return response;
    }
}