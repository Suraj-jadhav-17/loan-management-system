
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
import com.loanapp.loan_application.entity.loan.LoanStatus;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.entity.penalty.PenaltyCharges;
import com.loanapp.loan_application.entity.register.User;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.foreclosure.ForeClosureRequestRepo;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.repository.penalty.PenaltyChargesRepository;
import com.loanapp.loan_application.repository.register.UserRepository;
import com.loanapp.loan_application.service.emischeduler.EmiSchedulesService;
import com.loanapp.loan_application.service.foreclosure.ForeClosureService;
import com.loanapp.loan_application.service.foreclosure.LoanClosureService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ForeClosureServiceImpl implements ForeClosureService {
    private final ForeClosureRequestRepo foreClosureRequestRepo;
    private final LoanAccountRepository loanAccountRepository;
    private final EmiScheduleRepository emiScheduleRepository;
    private final LoanPaymentsRepository loanPaymentsRepository;
    private final PenaltyChargesRepository penaltyChargesRepository;
    private final UserRepository userRepository;
    private final LoanClosureService loanClosureService;
    private final EmiSchedulesService emiSchedulesService;
    private final ModelMapper modelMapper;

    @Value("${loan.foreclosure.fee-rate:2.0}")
    private BigDecimal foreclosureFeeRate;

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

        if (loanAccount.getLoanStatus() != LoanStatus.ACTIVE) {
            throw new RuntimeException("Foreclosure is allowed only for active loan");
        }

        BigDecimal principal = money(loanAccount.getOutstandingPrincipal());
        if (principal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Loan has no outstanding principal");
        }

        boolean pendingExists = foreClosureRequestRepo
                .existsByLoanAccountLoanAccountIdAndStatus(
                        request.getLoanAccountId(), ForeClosureStatus.PENDING);
        boolean approvedExists = foreClosureRequestRepo
                .existsByLoanAccountLoanAccountIdAndStatus(
                        request.getLoanAccountId(), ForeClosureStatus.APPROVED);

        if (pendingExists || approvedExists) {
            throw new RuntimeException("An active foreclosure request already exists");
        }

        ForeClosureRequest entity = ForeClosureRequest.builder()
                .loanAccount(loanAccount)
                .foreClosureType(request.getForeClosureType())
                .status(ForeClosureStatus.PENDING)
                .isPaid(false)
                .requestedAt(LocalDateTime.now())
                .build();

        if (request.getForeClosureType() == ForeClosureType.FULL) {
            applyFullSettlementQuote(entity, loanAccount);
        } else {
            BigDecimal partialAmount = request.getPartialAmount();
            if (partialAmount == null || partialAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Partial foreclosure amount must be greater than zero");
            }
            if (partialAmount.compareTo(principal) >= 0) {
                throw new RuntimeException("Partial amount must be less than outstanding principal");
            }

            partialAmount = money(partialAmount);
            entity.setPartialAmount(partialAmount);
            entity.setOutstandingPrincipal(partialAmount);
            entity.setAccruedInterest(money(BigDecimal.ZERO));
            entity.setOverdueDues(money(BigDecimal.ZERO));
            entity.setForeclosureFee(money(BigDecimal.ZERO));
            entity.setForeClosureAmount(partialAmount);
        }

        return mapToResponse(foreClosureRequestRepo.save(entity));
    }

    @Override
    public ForeClosureResponseDto approveRequest(ForeClosureApprovalDto request) {
        if (request == null || request.getForeClosureId() == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }
        validateOfficer(request.getOfficerId());

        ForeClosureRequest foreclosure = getForeclosureEntity(request.getForeClosureId());
        if (foreclosure.getStatus() != ForeClosureStatus.PENDING) {
            throw new RuntimeException("Only pending foreclosure request can be approved");
        }

        if (foreclosure.getForeClosureType() == ForeClosureType.FULL) {
            applyFullSettlementQuote(foreclosure, foreclosure.getLoanAccount());
        }

        foreclosure.setStatus(ForeClosureStatus.APPROVED);
        foreclosure.setClosedBy(request.getOfficerId());
        foreclosure.setApprovedAt(LocalDateTime.now());
        foreclosure.setRemarks(request.getRemarks());

        return mapToResponse(foreClosureRequestRepo.save(foreclosure));
    }

    @Override
    public ForeClosureResponseDto rejectRequest(ForeClosureApprovalDto request) {
        if (request == null || request.getForeClosureId() == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }
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
        if (request == null || request.getForeClosureId() == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }

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
        savePaymentRecord(loanAccount.getLoanAccountId(), paidAmount, foreclosure.getForeClosureType());

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

    private void applyFullSettlementQuote(ForeClosureRequest request, LoanAccount loanAccount) {
        BigDecimal principal = money(loanAccount.getOutstandingPrincipal());
        BigDecimal accruedInterest = calculateAccruedInterest(loanAccount, principal);
        BigDecimal overdueDues = calculateOverdueDues(loanAccount.getLoanAccountId());

        BigDecimal fee = principal.multiply(rate(foreclosureFeeRate))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal total = principal.add(accruedInterest)
                .add(overdueDues)
                .add(fee)
                .setScale(2, RoundingMode.HALF_UP);

        request.setOutstandingPrincipal(principal);
        request.setAccruedInterest(accruedInterest);
        request.setOverdueDues(overdueDues);
        request.setForeclosureFee(fee);
        request.setForeClosureAmount(total);
    }

    private BigDecimal calculateAccruedInterest(LoanAccount loanAccount, BigDecimal principal) {
        BigDecimal annualRate = loanAccount.getInterestRate();
        if (annualRate == null || annualRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Loan interest rate must be configured and cannot be negative");
        }

        List<EmiSchedules> schedules =
                emiScheduleRepository.findByLoanAccountId(loanAccount.getLoanAccountId());

        LocalDate today = LocalDate.now();
        LocalDate lastDueDate = schedules.stream()
                .map(EmiSchedules::getDueDate)
                .filter(date -> date != null && !date.isAfter(today))
                .max(Comparator.naturalOrder())
                .orElse(loanAccount.getDisbursementDate() == null
                        ? today : loanAccount.getDisbursementDate().toLocalDate());

        long days = Math.max(0, ChronoUnit.DAYS.between(lastDueDate, today));
        if (days == 0 || principal.compareTo(BigDecimal.ZERO) <= 0) {
            return money(BigDecimal.ZERO);
        }

        return principal.multiply(annualRate)
                .multiply(BigDecimal.valueOf(days))
                .divide(BigDecimal.valueOf(36500), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOverdueDues(Long loanAccountId) {
        LocalDate today = LocalDate.now();

        BigDecimal overdueInterest = emiScheduleRepository.findByLoanAccountId(loanAccountId)
                .stream()
                .filter(emi -> emi.getDueDate() != null && emi.getDueDate().isBefore(today))
                .filter(emi -> emi.getPaymentStatus() != PaymentStatus.PAID
                        && emi.getPaymentStatus() != PaymentStatus.CANCELLED)
                .map(EmiSchedules::getInterestAmount)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal activePenalties = penaltyChargesRepository.findByLoanAccountId(loanAccountId)
                .stream()
                .filter(penalty -> "ACTIVE".equalsIgnoreCase(penalty.getStatus()))
                .map(PenaltyCharges::getPenaltyAmount)
                .map(this::money)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return overdueInterest.add(activePenalties)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal money(BigDecimal amount) {
        return amount == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : amount.setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal rate(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value.max(BigDecimal.ZERO);
    }

    private void processFullForeclosure(ForeClosureRequest foreclosure, LoanAccount loanAccount) {
        BigDecimal payment = foreclosure.getForeClosureAmount();
        loanAccount.setOutstandingPrincipal(BigDecimal.ZERO);

        BigDecimal totalPaid = loanAccount.getTotalPaidAmount() == null
                ? BigDecimal.ZERO : loanAccount.getTotalPaidAmount();
        loanAccount.setTotalPaidAmount(totalPaid.add(payment));

        cancelPendingEmis(loanAccount.getLoanAccountId(), "Loan fully foreclosed");
        loanAccount.setLoanStatus(LoanStatus.CLOSED);
        loanAccountRepository.save(loanAccount);

        loanClosureService.closeLoanByForeclosure(
                loanAccount.getLoanAccountId(), foreclosure.getClosedBy());
    }

    private void processPartialForeclosure(LoanAccount loanAccount, BigDecimal partialAmount) {
        BigDecimal oldPrincipal = loanAccount.getOutstandingPrincipal();
        if (oldPrincipal == null || partialAmount == null
                || partialAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Invalid partial foreclosure amount");
        }

        BigDecimal newPrincipal = oldPrincipal.subtract(partialAmount);
        if (newPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Partial foreclosure cannot make balance zero");
        }

        loanAccount.setOutstandingPrincipal(newPrincipal);
        BigDecimal totalPaid = loanAccount.getTotalPaidAmount() == null
                ? BigDecimal.ZERO : loanAccount.getTotalPaidAmount();
        loanAccount.setTotalPaidAmount(totalPaid.add(partialAmount));
        loanAccount.setLoanStatus(LoanStatus.ACTIVE);
        loanAccountRepository.save(loanAccount);

        emiSchedulesService.regenerateAfterPartialForeclosure(
                loanAccount.getLoanAccountId(), newPrincipal);
    }

    private void cancelPendingEmis(Long loanAccountId, String reason) {
        List<EmiSchedules> schedules = emiScheduleRepository
                .findByLoanAccountIdAndPaymentStatus(loanAccountId, PaymentStatus.PENDING);

        for (EmiSchedules schedule : schedules) {
            schedule.setPaymentStatus(PaymentStatus.CANCELLED);
            schedule.setCancellationReason(reason);
        }
        emiScheduleRepository.saveAll(schedules);
    }

    private void savePaymentRecord(Long loanAccountId, BigDecimal amount, ForeClosureType type) {
        LoanPayments payment = new LoanPayments();
        payment.setLoanAccountId(loanAccountId);
        payment.setPaymentAmount(amount);
        payment.setPaymentStatus(PaymentStatus.PAID);
        payment.setPaymentName(type == ForeClosureType.FULL
                ? "FULL_FORECLOSURE" : "PARTIAL_FORECLOSURE");
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
                && !"LOAN OFFICER".equalsIgnoreCase(role)
                && !"LOANOFFICER".equalsIgnoreCase(role)) {
            throw new RuntimeException("Only Admin or Loan Officer can approve foreclosure");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ForeClosureResponseDto getRequest(Long foreClosureId) {
        return mapToResponse(getForeclosureEntity(foreClosureId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ForeClosureResponseDto> getPendingRequests() {
        return foreClosureRequestRepo.findByStatus(ForeClosureStatus.PENDING)
                .stream().map(this::mapToResponse).toList();
    }

    private ForeClosureRequest getForeclosureEntity(Long foreClosureId) {
        if (foreClosureId == null) {
            throw new RuntimeException("Foreclosure ID is required");
        }
        return foreClosureRequestRepo.findById(foreClosureId)
                .orElseThrow(() -> new RuntimeException("Foreclosure request not found"));
    }

    private ForeClosureResponseDto mapToResponse(ForeClosureRequest entity) {
        ForeClosureResponseDto response =
                modelMapper.map(entity, ForeClosureResponseDto.class);
        response.setLoanAccountId(entity.getLoanAccount().getLoanAccountId());
        return response;
    }
}
