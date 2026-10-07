package com.loanapp.loan_application.serviceimpl.multiplepayment;

import com.loanapp.loan_application.dto.multiplepayment.MultipleEmiRequestDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.multiplepayment.MultipleEmiRequest;
import com.loanapp.loan_application.entity.penalty.PenaltyCharges;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.multiplepayment.MultipleEmiRequestRepository;
import com.loanapp.loan_application.repository.penalty.PenaltyChargesRepository;
import com.loanapp.loan_application.service.multiplepayment.MultipleEmiRequestService;
import com.loanapp.loan_application.service.payment.RazorpayService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.loanapp.loan_application.entity.payment.PaymentStatus.PAID;

@Service
public class MultipleEmiRequestServiceImpl
        implements MultipleEmiRequestService {

    private final MultipleEmiRequestRepository repo;
    private final EmiScheduleRepository emiRepo;
    private final PenaltyChargesRepository penaltyRepo;
    private final RazorpayService razorpayService;

    public MultipleEmiRequestServiceImpl(
            MultipleEmiRequestRepository repo,
            EmiScheduleRepository emiRepo,
            PenaltyChargesRepository penaltyRepo,
            RazorpayService razorpayService) {

        this.repo = repo;
        this.emiRepo = emiRepo;
        this.penaltyRepo = penaltyRepo;
        this.razorpayService = razorpayService;
    }

    @Override
    public MultipleEmiRequestDto request(MultipleEmiRequestDto dto) {
        String[] ids = dto.getEmiIds().split(",");
        BigDecimal total = BigDecimal.ZERO;
        for (String id : ids) {
            Long emiId = Long.valueOf(id.trim());
            EmiSchedules emi = emiRepo.findById(emiId).orElseThrow(() ->
                            new RuntimeException("EMI not found: " + emiId));
            if (!emi.getLoanAccountId().equals(dto.getLoanAccountId())) {
                throw new RuntimeException("EMI does not belong to this loan");
            }

            if ("PAID".equalsIgnoreCase(emi.getPaymentStatus())) {
                throw new RuntimeException("EMI already paid: " + emiId);
            }
            total = total.add(emi.getEmi());
            List<PenaltyCharges> penalties = penaltyRepo.findByEmiScheduleId(emiId);
            for (var penalty : penalties) {
                if (penalty.getPenaltyAmount() != null) {
                    total = total.add(penalty.getPenaltyAmount());
                }
            }
        }

        MultipleEmiRequest request = new MultipleEmiRequest();
        request.setLoanAccountId(dto.getLoanAccountId());
        request.setEmiIds(dto.getEmiIds());
        request.setTotalAmount(total);
        request.setApprovalStatus("PENDING");
        request.setPaymentStatus("NOT_PAID");
        request.setRequestedDate(LocalDateTime.now());
        MultipleEmiRequest saved = repo.save(request);
        return convert(saved);
    }

    @Override
    public List<MultipleEmiRequestDto> getByLoan(Long loanAccountId) {
        return repo.findByLoanAccountId(loanAccountId)
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public MultipleEmiRequestDto approve(Long requestId) {
        MultipleEmiRequest request = repo.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"PENDING".equalsIgnoreCase(request.getApprovalStatus())) {
            throw new RuntimeException("Request is not pending");
        }
        request.setApprovalStatus("APPROVED");
        request.setApprovedDate(LocalDateTime.now());
        MultipleEmiRequest saved = repo.save(request);
        return convert(saved);
    }

    @Override
    public String createOrder(Long requestId) {

        MultipleEmiRequest request = repo.findById(requestId).orElseThrow(() -> new RuntimeException("Request not found"));

        if (!"APPROVED".equalsIgnoreCase(request.getApprovalStatus())) {
            throw new RuntimeException("Request is not approved");
        }

        String firstEmiId = request.getEmiIds().split(",")[0].trim();
        return razorpayService.createOrder(request.getTotalAmount(), request.getLoanAccountId(), Long.valueOf(firstEmiId));
    }

    private MultipleEmiRequestDto convert(MultipleEmiRequest request) {
        MultipleEmiRequestDto dto = new MultipleEmiRequestDto();
        dto.setRequestId(request.getRequestId());
        dto.setLoanAccountId(request.getLoanAccountId());
        dto.setEmiIds(request.getEmiIds());
        dto.setTotalAmount(request.getTotalAmount());
        dto.setApprovalStatus(request.getApprovalStatus());
        dto.setPaymentStatus(request.getPaymentStatus());
        dto.setRequestedDate(request.getRequestedDate());
        dto.setApprovedDate(request.getApprovedDate());
        return dto;
    }
    @Override
    public MultipleEmiRequestDto verifyPayment(Long requestId, String orderId, String paymentId, String signature) {
        MultipleEmiRequest request = repo.findById(requestId).orElseThrow(() ->
                new RuntimeException("Request not found"));
        if (!"APPROVED".equalsIgnoreCase(request.getApprovalStatus())) {
            throw new RuntimeException("Request is not approved");
        }
        boolean verified = razorpayService.verifySignature(orderId, paymentId, signature);
        if (!verified) {throw new RuntimeException(
                    "Payment verification failed"
            );
        }

        String[] ids = request.getEmiIds().split(",");
        for (String id : ids) {
            Long emiId = Long.valueOf(id.trim());
            EmiSchedules emi = emiRepo.findById(emiId)
                            .orElseThrow(() -> new RuntimeException(
                                    "EMI not found: " + emiId));
            if ("PAID".equalsIgnoreCase(emi.getPaymentStatus())) {
                continue;
            }
            emi.setPaymentStatus(PAID);
            emi.setPaidDate(LocalDateTime.now());
            emiRepo.save(emi);
        }
        request.setPaymentStatus("PAID");
        MultipleEmiRequest saved = repo.save(request);
        return convert(saved);
    }


}