//package com.loanapp.loan_application.serviceimpl.loanaccount;
//
//
//import com.loanapp.loan_application.dto.emischeduler.ForeClosurePaymentDto;
//import com.loanapp.loan_application.dto.foreclosure.ForeClosureResponseDto;
//import com.loanapp.loan_application.dto.request.loanaccount.LoanAccountRequestDto;
//import com.loanapp.loan_application.entity.foreclosure.ForeClosureRequest;
//import com.loanapp.loan_application.entity.foreclosure.ForeClosureStatus;
//import com.loanapp.loan_application.entity.foreclosure.ForeClosureType;
//import com.loanapp.loan_application.entity.loanaccount.LoanAccount;
//import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
//import com.loanapp.loan_application.repository.loanaccount.ForeClosureRequestRepo;
//import com.loanapp.loan_application.repository.loanaccount.LoanAccountRepository;
//import com.loanapp.loan_application.service.loanaccount.ForeClosureService;
//import com.loanapp.loan_application.service.loanaccount.LoanClosureService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class ForeClosureServiceImpl implements ForeClosureService {
//
//    private final ForeClosureRequestRepo foreClosureRequestRepo;
//
//    private final LoanAccountRepository loanAccountRepository;
//
//    private final EmiScheduleRepository emiScheduleRepository;
//
//    private final LoanClosureService loanClosureService;
//
//
//
//
//    @Override
//    public ForeClosureResponseDto createRequest(LoanAccountRequestDto.ForeClosureRequestDto request) {
//
//        if (request == null ||
//                request.getLoanAccountId() == null) {
//            throw new RuntimeException("Loan Account ID is required");
//        }
//
//        if (request.getForeClosureType() == null) {
//            throw new RuntimeException("Foreclosure type is required");
//        }
//
//
//        LoanAccount loanAccount = loanAccountRepository.findById(request.getLoanAccountId())
//                        .orElseThrow(() -> new RuntimeException("Loan account not found"));
//
//
//
//        if (!"ACTIVE".equalsIgnoreCase(loanAccount.getLoanStatus())) {
//            throw new RuntimeException("Foreclosure is allowed only for active loan");
//        }
//
//
//        BigDecimal outstandingPrincipal = loanAccount.getOutstandingPrincipal();
//
//
//        if (outstandingPrincipal == null || outstandingPrincipal.compareTo(BigDecimal.ZERO) <= 0) {
//            throw new RuntimeException("Loan has no outstanding principal");
//        }
//
//
//        BigDecimal foreClosureAmount = null;
//
//        BigDecimal partialAmount = null;
//
//
//
//
//        if (request.getForeClosureType() == ForeClosureType.FULL) {
//
//            foreClosureAmount = calculateFullForeClosureAmount(loanAccount);
//        }
//
//
//
//        if (request.getForeClosureType() == ForeClosureType.PARTIAL) {
//
//            partialAmount = request.getPartialAmount();
//
//
//            if (partialAmount == null || partialAmount.compareTo(BigDecimal.ZERO) <= 0) {
//                throw new RuntimeException("Partial foreclosure amount must be greater than zero");
//            }
//
//
//            if (partialAmount.compareTo(outstandingPrincipal) >= 0) {
//                throw new RuntimeException("Partial amount must be less than outstanding principal");
//            }
//
//
//            foreClosureAmount = partialAmount;
//        }
//
//
//        ForeClosureRequest foreclosure = ForeClosureRequest.builder().loanAccount(loanAccount)
//                .foreClosureType(request.getForeClosureType())
//                .foreClosureAmount(foreClosureAmount)
//                .partialAmount(partialAmount)
//                .status(ForeClosureStatus.PENDING)
//                .isPaid(false)
//                .requestedAt(LocalDateTime.now())
//                .build();
//
//
//        ForeClosureRequest saved = foreClosureRequestRepo.save(foreclosure);
//
//
//        return mapToResponse(saved);
//    }
//
//
//
//    private BigDecimal calculateFullForeClosureAmount(LoanAccount loanAccount) {
//
//        BigDecimal principal = loanAccount.getOutstandingPrincipal();
//
//
//        if (principal == null) {
//            principal = BigDecimal.ZERO;
//        }
//
//        return principal;
//    }
//
//
//    @Override
//    public ForeClosureResponseDto approveRequest(LoanAccountRequestDto.ForeClosureApprovalDto request
//    ) {
//        ForeClosureRequest foreclosure = getForeclosureEntity(request.getForeClosureId());
//
//
//        if (foreclosure.getStatus() != ForeClosureStatus.PENDING) {
//            throw new RuntimeException("Only pending foreclosure request can be approved");
//        }
//
//
//        if (!Boolean.TRUE.equals(request.getApproved()
//        )) {
//
//            throw new RuntimeException("Use reject endpoint for rejection");
//        }
//
//
//        foreclosure.setStatus(ForeClosureStatus.APPROVED);
//        foreclosure.setClosedBy(request.getOfficerId());
//        foreclosure.setApprovedAt(LocalDateTime.now());
//        foreclosure.setRemarks(request.getRemarks());
//
//
//        return mapToResponse(foreClosureRequestRepo.save(foreclosure));
//    }
//
//
//
//
//    @Override
//    public ForeClosureResponseDto rejectRequest(
//            LoanAccountRequestDto.ForeClosureApprovalDto request
//    ) {
//
//        ForeClosureRequest foreclosure =
//                getForeclosureEntity(
//                        request.getForeClosureId()
//                );
//
//
//        if (foreclosure.getStatus()
//                != ForeClosureStatus.PENDING) {
//
//            throw new RuntimeException(
//                    "Only pending foreclosure request can be rejected"
//            );
//        }
//
//
//        foreclosure.setStatus(
//                ForeClosureStatus.REJECTED
//        );
//
//        foreclosure.setClosedBy(
//                request.getOfficerId()
//        );
//
//        foreclosure.setRemarks(
//                request.getRemarks()
//        );
//
//
//        return mapToResponse(
//                foreClosureRequestRepo.save(
//                        foreclosure
//                )
//        );
//    }
//
//
//
//
//    @Override
//    public ForeClosureResponseDto makePayment(
//            ForeClosurePaymentDto request
//    ) {
//
//        ForeClosureRequest foreclosure =
//                getForeclosureEntity(
//                        request.getForeClosureId()
//                );
//
//
//        if (foreclosure.getStatus()
//                != ForeClosureStatus.APPROVED) {
//
//            throw new RuntimeException(
//                    "Foreclosure request must be approved before payment"
//            );
//        }
//
//
//        if (Boolean.TRUE.equals(
//                foreclosure.getIsPaid()
//        )) {
//
//            throw new RuntimeException(
//                    "Foreclosure payment is already completed"
//            );
//        }
//
//
//        BigDecimal requiredAmount =
//                foreclosure.getForeClosureAmount();
//
//
//        BigDecimal paidAmount =
//                request.getPaidAmount();
//
//
//        if (paidAmount == null ||
//                paidAmount.compareTo(
//                        requiredAmount
//                ) != 0) {
//
//            throw new RuntimeException(
//                    "Paid amount must exactly match foreclosure amount"
//            );
//        }
//
//
//        LoanAccount loanAccount =
//                foreclosure.getLoanAccount();
//
//
//
//
//        if (foreclosure.getForeClosureType()
//                == ForeClosureType.FULL) {
//
//            processFullForeclosure(
//                    foreclosure,
//                    loanAccount
//            );
//        }
//
//        else {
//
//            processPartialForeclosure(
//                    foreclosure,
//                    loanAccount,
//                    paidAmount
//            );
//        }
//
//
//        foreclosure.setIsPaid(true);
//
//        foreclosure.setStatus(
//                ForeClosureStatus.PAID
//        );
//
//        foreclosure.setPaymentDate(
//                LocalDateTime.now()
//        );
//
//
//        ForeClosureRequest saved =
//                foreClosureRequestRepo.save(
//                        foreclosure
//                );
//
//
////        return mapToResponse(saved);
////    }
//
//    @Override
//    public ForeClosureResponseDto getRequest(Long foreClosureId) {
//        return null;
//    }
//
//
//    private void processFullForeclosure(
//            ForeClosureRequest foreclosure,
//            LoanAccount loanAccount
//    ) {
//
//
//        loanAccount.setOutstandingPrincipal(
//                BigDecimal.ZERO
//        );
//
//
//
//        BigDecimal totalPaid =
//                loanAccount.getTotalPaidAmount();
//
//        if (totalPaid == null) {
//            totalPaid = BigDecimal.ZERO;
//        }
//
//
//        loanAccount.setTotalPaidAmount(
//                totalPaid.add(
//                        foreclosure.getForeClosureAmount()
//                )
//        );
//
//
//
////        List<EmiSchedule> schedules =
////                emiScheduleRepository
////                        .findByLoanAccountLoanAccountId(
////                                loanAccount.getLoanAccountId()
////                        );
//
//
////        for (EmiSchedule schedule : schedules) {
////
////            if (!"PAID".equalsIgnoreCase(
////                    schedule.getPaymentStatus()
////            )) {
////
////                schedule.setPaymentStatus(
////                        "CANCELLED"
////                );
//
////                emiScheduleRepository.save(
////                        schedule
////                );
////            }
////        }
//
//
//        loanAccount.setLoanStatus(
//                "CLOSED"
//        );
//
//
//        loanAccountRepository.save(
//                loanAccount
//        );
//
//
//
//        loanClosureService.closeLoanByForeclosure(
//                loanAccount.getLoanAccountId(),
//                foreclosure.getClosedBy()
//        );
//    }
//
//
//
//
//    private void processPartialForeclosure(
//            ForeClosureRequest foreclosure,
//            LoanAccount loanAccount,
//            BigDecimal partialAmount
//    ) {
//
//        BigDecimal oldPrincipal =
//                loanAccount.getOutstandingPrincipal();
//
//
//        BigDecimal newPrincipal =
//                oldPrincipal.subtract(
//                        partialAmount
//                );
//
//
//        if (newPrincipal.compareTo(
//                BigDecimal.ZERO
//        ) <= 0) {
//
//            throw new RuntimeException(
//                    "Partial foreclosure cannot make loan balance zero"
//            );
//        }
//
//
//
//        loanAccount.setOutstandingPrincipal(
//                newPrincipal
//        );
//
//
//
//        BigDecimal totalPaid =
//                loanAccount.getTotalPaidAmount();
//
//
//        if (totalPaid == null) {
//            totalPaid = BigDecimal.ZERO;
//        }
//
//
//        loanAccount.setTotalPaidAmount(
//                totalPaid.add(partialAmount)
//        );
//
//
//        loanAccount.setLoanStatus(
//                "ACTIVE"
//        );
//
//
//        loanAccountRepository.save(
//                loanAccount
//        );
//
////        regenerateEmiSchedule(
////                loanAccount,
////                newPrincipal
////        );
////    }
//
//
//
//
////    private void regenerateEmiSchedule(
////            LoanAccount loanAccount,
////            BigDecimal newPrincipal
////    ) {
////
////
////
////        List<EmiSchedule> schedules =
////                emiScheduleRepository
////                        .findByLoanAccountLoanAccountId(
////                                loanAccount.getLoanAccountId()
////                        );
////
////
////        for (EmiSchedule schedule : schedules) {
////
////            if (!"PAID".equalsIgnoreCase(
////                    schedule.getPaymentStatus()
////            )) {
////
////                schedule.setPaymentStatus(
////                        "CANCELLED"
////                );
////
////                emiScheduleRepository.save(
////                        schedule
////                );
////            }
////        }
//
//
////    @Override
////    @Transactional(readOnly = true)
////    public ForeClosureResponseDto getRequest(
////            Long foreClosureId
////    ) {
////
////        return mapToResponse(
////                getForeclosureEntity(
////                        foreClosureId
////                )
////        );
////    }
////
////
////    private ForeClosureRequest getForeclosureEntity(
////            Long foreClosureId
////    ) {
////
////        if (foreClosureId == null) {
////
////            throw new RuntimeException(
////                    "Foreclosure ID is required"
////            );
////        }
////
////
////        return foreClosureRequestRepo
////                .findById(foreClosureId)
////                .orElseThrow(() ->
////                        new RuntimeException(
////                                "Foreclosure request not found"
////                        )
////                );
////    }
//
//
////    private ForeClosureResponseDto mapToResponse(
////            ForeClosureRequest entity
////    ) {
//
////        return ForeClosureResponseDto.builder()
////
////                .foreClosureId(
////                        entity.getForeClosureId()
////                )
////
////                .loanAccountId(
////                        entity.getLoanAccount()
////                                .getLoanAccountId()
////                )
////
////                .foreClosureType(
////                        entity.getForeClosureType()
////                )
////
////                .foreClosureAmount(
////                        entity.getForeClosureAmount()
////                )
////
////                .partialAmount(
////                        entity.getPartialAmount()
////                )
////
////                .status(
////                        entity.getStatus()
////                )
////
////                .isPaid(
////                        entity.getIsPaid()
////                )
////
////                .requestedAt(
////                        entity.getRequestedAt()
////                )
////
////                .approvedAt(
////                        entity.getApprovedAt()
////                )
////
////                .closedBy(
////                        entity.getClosedBy()
////                )
////
////                .paymentDate(
////                        entity.getPaymentDate()
////                )
////
////                .remarks(
////                        entity.getRemarks()
////                )
////
////                .build();
//    }
//}