package com.loanapp.loan_application.serviceimpl.penalty;

import com.loanapp.loan_application.dto.penalty.PenaltyChargesDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.penalty.PenaltyCharges;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.penalty.PenaltyChargesRepository;
import com.loanapp.loan_application.service.penalty.PenaltyChargesService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PenaltyChargesServiceImpl implements PenaltyChargesService {

    private final PenaltyChargesRepository repo;
    private final EmiScheduleRepository emiRepo;

    public PenaltyChargesServiceImpl(
            PenaltyChargesRepository repo,
            EmiScheduleRepository emiRepo) {

        this.repo = repo;
        this.emiRepo = emiRepo;
    }

    @Override
    public PenaltyChargesDto add(PenaltyChargesDto dto) {
        if (repo.existsByEmiScheduleId(dto.getEmiScheduleId())) {
            throw new RuntimeException("Penalty already exists for this EMI");
        }

        EmiSchedules emi = emiRepo.findById(dto.getEmiScheduleId()).orElseThrow(() ->
                new RuntimeException("EMI not found"));

        long days = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());
        if (days < 0) {days = 0;}
        BigDecimal lateCharge = BigDecimal.valueOf(days);
        long months = ChronoUnit.MONTHS.between(
                emi.getDueDate().withDayOfMonth(1),
                LocalDate.now().withDayOfMonth(1));
        if (months < 1) {months = 1;}
        BigDecimal bounceCharge = BigDecimal.valueOf(months * 500);
        BigDecimal total = lateCharge.add(bounceCharge);
        PenaltyCharges penalty = new PenaltyCharges();
        penalty.setEmiScheduleId(dto.getEmiScheduleId());
        penalty.setLoanAccountId(dto.getLoanAccountId());
        penalty.setPenaltyAmount(total);
        penalty.setReason("Bounce charge: Rs " + bounceCharge + ", Late charge: Rs " + lateCharge);

        penalty.setStatus("ACTIVE");
        penalty.setCreatedAt(LocalDateTime.now());
        PenaltyCharges saved = repo.save(penalty);
        return convert(saved);
    }

    @Override
    public List<PenaltyChargesDto> getByLoan(Long loanAccountId) {
        List<PenaltyCharges> list = repo.findByLoanAccountId(loanAccountId);
        return list.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public List<PenaltyChargesDto> getByEmi(Long emiScheduleId) {
        List<PenaltyCharges> list = repo.findByEmiScheduleId(emiScheduleId);
        return list.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public void addOverdue() {
        List<EmiSchedules> list = emiRepo.findAll();
        for (EmiSchedules emi : list) {
            if (emi.getDueDate().isBefore(LocalDate.now()) && emi.getPaymentStatus()!= PaymentStatus.PAID) {
                long days = ChronoUnit.DAYS.between(emi.getDueDate(), LocalDate.now());
                BigDecimal lateCharge = BigDecimal.valueOf(days);
                long months = ChronoUnit.MONTHS.between(emi.getDueDate().withDayOfMonth(1), LocalDate.now().withDayOfMonth(1));
                if (months < 1) {months = 1;}
                BigDecimal bounceCharge = BigDecimal.valueOf(months * 500);
                BigDecimal total = lateCharge.add(bounceCharge);
                List<PenaltyCharges> existing = repo.findByEmiScheduleId(emi.getEmiScheduleId());
                if (existing.isEmpty()) {
                    PenaltyCharges penalty = new PenaltyCharges();
                    penalty.setEmiScheduleId(emi.getEmiScheduleId());
                    penalty.setLoanAccountId(emi.getLoanAccountId());
                    penalty.setPenaltyAmount(total);
                    penalty.setReason("Bounce charge: Rs" + bounceCharge + ", Late charge: Rs" + lateCharge);
                    penalty.setStatus("ACTIVE");
                    penalty.setCreatedAt(LocalDateTime.now());
                    repo.save(penalty);

                } else {
                    PenaltyCharges penalty = existing.get(0);
                    penalty.setPenaltyAmount(total);
                    penalty.setReason("Bounce charge: " + bounceCharge + ", Late charge: " + lateCharge);
                    penalty.setCreatedAt(LocalDateTime.now());
                    repo.save(penalty);
                }
            }
        }
    }

    private PenaltyChargesDto convert(PenaltyCharges penalty) {
        PenaltyChargesDto dto = new PenaltyChargesDto();
        dto.setPenaltyChargeId(penalty.getPenaltyChargeId());
        dto.setEmiScheduleId(penalty.getEmiScheduleId());
        dto.setLoanAccountId(penalty.getLoanAccountId());
        dto.setPenaltyAmount(penalty.getPenaltyAmount());
        dto.setReason(penalty.getReason());
        dto.setStatus(penalty.getStatus());
        dto.setCreatedAt(penalty.getCreatedAt());
        return dto;
    }
}