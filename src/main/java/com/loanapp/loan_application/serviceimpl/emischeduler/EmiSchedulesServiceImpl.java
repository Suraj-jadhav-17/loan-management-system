package com.loanapp.loan_application.serviceimpl.emischeduler;

import com.loanapp.loan_application.dto.emischeduler.EmiSchedulesDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
import com.loanapp.loan_application.service.emischeduler.EmiSchedulesService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EmiSchedulesServiceImpl implements EmiSchedulesService {

    private final EmiScheduleRepository repo;
    private final LoanAccountRepository loanRepo;

    public EmiSchedulesServiceImpl(
            EmiScheduleRepository repo,
            LoanAccountRepository loanRepo) {
        this.repo = repo;
        this.loanRepo = loanRepo;
    }

    @Override
    public List<EmiSchedulesDto> getByLoanId(Long loanAccountId) {

        List<EmiSchedules> list =
                repo.findByLoanAccountId(loanAccountId);

        return list.stream().map(e -> {

            EmiSchedulesDto dto = new EmiSchedulesDto();

            dto.setLoanAccountId(e.getLoanAccountId());
            dto.setInstallmentNo(e.getInstallmentNo());
            dto.setDueDate(e.getDueDate());
            dto.setPrincipalAmount(e.getPrincipalAmount());
            dto.setInterestAmount(e.getInterestAmount());
            dto.setOpeningBalance(e.getOpeningBalance());
            dto.setClosingBalance(e.getClosingBalance());
            dto.setEmi(e.getEmi());
            dto.setPaymentStatus(e.getPaymentStatus());

            return dto;

        }).collect(Collectors.toList());
    }

    @Override
    public void generate(Long loanAccountId) {

        // Agar schedule already generated hai toh dobara generate nahi karna
        if (repo.existsByLoanAccountId(loanAccountId)) {
            return;
        }

        // LoanAccount se loan details lena
        LoanAccount loan = loanRepo.findById(loanAccountId)
                .orElseThrow(() ->
                        new RuntimeException("Loan account not found"));

        BigDecimal balance = loan.getLoanAmount();
        BigDecimal rate = loan.getInterestRate();
        BigDecimal emi = loan.getEmiAmount();
        int tenure = loan.getTenureMonths();

        LocalDate dueDate = loan.getDisbursementDate()
                .toLocalDate()
                .plusMonths(1);

        // Annual interest rate ko monthly rate mein convert karna
        BigDecimal monthlyRate = rate.divide(
                BigDecimal.valueOf(12 * 100),
                10,
                RoundingMode.HALF_UP
        );

        // Har month ki EMI schedule create karna
        for (int i = 1; i <= tenure; i++) {

            BigDecimal openingBalance = balance;

            BigDecimal interest = openingBalance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principal = emi
                    .subtract(interest)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal closingBalance = openingBalance
                    .subtract(principal)
                    .setScale(2, RoundingMode.HALF_UP);

            EmiSchedules schedule = new EmiSchedules();

            schedule.setLoanAccountId(loanAccountId);
            schedule.setInstallmentNo((long) i);
            schedule.setDueDate(dueDate);

            schedule.setOpeningBalance(openingBalance);
            schedule.setInterestAmount(interest);
            schedule.setPrincipalAmount(principal);

            schedule.setEmi(emi);
            schedule.setClosingBalance(closingBalance);

            schedule.setPaymentStatus(PaymentStatus.PENDING);
            repo.save(schedule);

            balance = closingBalance;
            dueDate = dueDate.plusMonths(1);
        }
    }

    @Override
    public void checkDefault(Long loanAccountId) {

        LoanAccount loan = loanRepo.findById(loanAccountId)
                .orElseThrow(() ->
                        new RuntimeException("Loan account not found"));

        List<EmiSchedules> emis = repo.findByLoanAccountId(loanAccountId);
        LocalDate today = LocalDate.now();
        for (EmiSchedules emi : emis) {
            if (emi.getPaymentStatus() == PaymentStatus.PAID) {
                continue;
            }
            LocalDate threeMonthsAfterDueDate =
                    emi.getDueDate().plusMonths(3);

            if (!today.isBefore(threeMonthsAfterDueDate)) {
                loan.setLoanStatus("CLOSED");
                loanRepo.save(loan);
                throw new RuntimeException(
                        "Loan account is closed due to  unpaid EMI. " +
                                "Please contact Loan Officer.");
            }
        }
    }

    @Override
    public void regenerateAfterPartialForeclosure(Long loanAccountId, BigDecimal newOutstandingPrincipal) {
        LoanAccount loan = loanRepo.findById(loanAccountId).orElseThrow(() ->
                        new RuntimeException("Loan account not found"));
        List<EmiSchedules> schedules = repo.findByLoanAccountId(loanAccountId);

        long nextInstallmentNo = schedules.stream()
                .filter(e -> e.getPaymentStatus() == PaymentStatus.PAID)
                .mapToLong(EmiSchedules::getInstallmentNo)
                .max()
                .orElse(0L) + 1;

        // First unpaid EMI ki existing due date
        LocalDate dueDate = schedules.stream()
                .filter(e -> e.getPaymentStatus() != PaymentStatus.PAID)
                .map(EmiSchedules::getDueDate)
                .min(LocalDate::compareTo)
                .orElse(loan.getDisbursementDate()
                                .toLocalDate()
                                .plusMonths(1));

        schedules.stream()
                .filter(e -> e.getPaymentStatus() != PaymentStatus.PAID)
                .forEach(e -> {
                    e.setPaymentStatus(PaymentStatus.CANCELLED);
                    e.setCancellationReason(
                            "Regenerated after partial foreclosure"
                    );
                    repo.save(e);
                });

        // Partial foreclosure ke baad new outstanding principal
        BigDecimal balance = newOutstandingPrincipal;


        BigDecimal emi = loan.getEmiAmount();

        BigDecimal monthlyRate = loan.getInterestRate()
                .divide(BigDecimal.valueOf(1200),
                        10,
                        RoundingMode.HALF_UP
                );


        while (balance.compareTo(BigDecimal.ZERO) > 0) {

            BigDecimal openingBalance = balance;

            BigDecimal interest = openingBalance
                    .multiply(monthlyRate)
                    .setScale(2, RoundingMode.HALF_UP);

            BigDecimal principal = emi.subtract(interest);

            BigDecimal currentEmi = emi;


            if (principal.compareTo(balance) > 0) {
                principal = balance;
                currentEmi = principal.add(interest);
            }

            BigDecimal closingBalance =
                    openingBalance.subtract(principal);

            EmiSchedules schedule = new EmiSchedules();

            schedule.setLoanAccountId(loanAccountId);
            schedule.setInstallmentNo(nextInstallmentNo);
            schedule.setDueDate(dueDate);
            schedule.setOpeningBalance(openingBalance);
            schedule.setInterestAmount(interest);
            schedule.setPrincipalAmount(principal);
            schedule.setEmi(currentEmi);
            schedule.setClosingBalance(closingBalance);
            schedule.setPaymentStatus(PaymentStatus.PENDING);
            schedule.setCancellationReason(null);

            repo.save(schedule);

            balance = closingBalance;
            nextInstallmentNo++;
            dueDate = dueDate.plusMonths(1);
        }
    }
}