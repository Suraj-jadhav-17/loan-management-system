
package com.loanapp.loan_application.serviceimpl.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;
import com.loanapp.loan_application.entity.emischeduler.EmiSchedules;
import com.loanapp.loan_application.entity.loan.LoanAccount;
import com.loanapp.loan_application.entity.payment.LoanPayments;
import com.loanapp.loan_application.entity.payment.PaymentStatus;
import com.loanapp.loan_application.repository.emischedulerepository.EmiScheduleRepository;
import com.loanapp.loan_application.repository.loan.LoanAccountRepository;
import com.loanapp.loan_application.repository.payment.LoanPaymentsRepository;
import com.loanapp.loan_application.service.payment.RazorpayService;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final LoanPaymentsRepository paymentsRepository;
    private final EmiScheduleRepository emiRepository;
    private final LoanAccountRepository loanRepository;

    public RazorpayServiceImpl(
            LoanPaymentsRepository paymentsRepository,
            EmiScheduleRepository emiRepository,
            LoanAccountRepository loanRepository) {
        this.paymentsRepository = paymentsRepository;
        this.emiRepository = emiRepository;
        this.loanRepository = loanRepository;
    }

    @Override
    public String createOrder(
            BigDecimal amount,
            Long loanAccountId,
            Long emiScheduleId) {

        try {
            if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0
                    || loanAccountId == null || emiScheduleId == null) {
                throw new IllegalArgumentException("Invalid payment details");
            }

            LoanAccount loan = loanRepository.findById(loanAccountId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("Loan account not found"));

            EmiSchedules emi = emiRepository.findById(emiScheduleId)
                    .orElseThrow(() ->
                            new IllegalArgumentException("EMI schedule not found"));

            if (!loanAccountId.equals(emi.getLoanAccountId())) {
                throw new IllegalArgumentException(
                        "EMI does not belong to this loan account");
            }

            if (emi.getPaymentStatus() == PaymentStatus.PAID) {
                throw new IllegalStateException("This EMI is already paid");
            }

            BigDecimal validAmount = amount.setScale(2, RoundingMode.UNNECESSARY);
            BigDecimal emiAmount = emi.getEmi().setScale(2, RoundingMode.UNNECESSARY);

            if (validAmount.compareTo(emiAmount) != 0) {
                throw new IllegalArgumentException("Payment amount does not match EMI");
            }

            long amountInPaise = validAmount
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put(
                    "receipt",
                    "loan_" + loanAccountId + "_emi_" + emiScheduleId);

            Order order = client.orders.create(orderRequest);
            return order.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Unable to create Razorpay order", e);
        }
    }

    @Override
    public boolean verifySignature(
            String orderId,
            String paymentId,
            String signature) {

        if (orderId == null || orderId.isBlank()
                || paymentId == null || paymentId.isBlank()
                || signature == null || signature.isBlank()) {
            return false;
        }

        try {
            return Utils.verifySignature(
                    orderId + "|" + paymentId,
                    signature,
                    keySecret);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean verifyForeclosurePayment(
            String orderId,
            String paymentId,
            String signature,
            BigDecimal expectedAmount) {

        try {
            if (expectedAmount == null
                    || expectedAmount.compareTo(BigDecimal.ZERO) <= 0
                    || !verifySignature(orderId, paymentId, signature)) {
                return false;
            }

            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            Payment payment = client.payments.fetch(paymentId);

            long expectedPaise = expectedAmount
                    .setScale(2, RoundingMode.UNNECESSARY)
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            return orderId.equals(String.valueOf(payment.get("order_id")))
                    && "INR".equalsIgnoreCase(
                    String.valueOf(payment.get("currency")))
                    && expectedPaise == Long.parseLong(
                    String.valueOf(payment.get("amount")))
                    && "captured".equalsIgnoreCase(
                    String.valueOf(payment.get("status")));

        } catch (Exception e) {
            throw new RuntimeException(
                    "Foreclosure payment verification failed", e);
        }
    }

    @Override
    @Transactional
    public LoanPaymentsDto verifyPayment(
            String orderId,
            String paymentId,
            String signature,
            LoanPaymentsDto dto) {

        try {
            if (dto == null
                    || dto.getLoanAccountId() == null
                    || dto.getEmiScheduleId() == null
                    || dto.getPaymentAmount() == null
                    || dto.getPaymentAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Invalid payment details");
            }

            if (!verifySignature(orderId, paymentId, signature)) {
                throw new IllegalArgumentException("Invalid Razorpay signature");
            }

            LoanAccount loan = loanRepository.findById(dto.getLoanAccountId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("Loan account not found"));

            EmiSchedules emi = emiRepository.findById(dto.getEmiScheduleId())
                    .orElseThrow(() ->
                            new IllegalArgumentException("EMI schedule not found"));

            if (!dto.getLoanAccountId().equals(emi.getLoanAccountId())) {
                throw new IllegalArgumentException(
                        "EMI does not belong to this loan account");
            }

            if (emi.getPaymentStatus() == PaymentStatus.PAID) {
                throw new IllegalStateException("This EMI is already paid");
            }

            BigDecimal requestedAmount = dto.getPaymentAmount()
                    .setScale(2, RoundingMode.UNNECESSARY);

            if (emi.getEmi() == null
                    || requestedAmount.compareTo(
                    emi.getEmi().setScale(2, RoundingMode.UNNECESSARY)) != 0) {
                throw new IllegalArgumentException(
                        "Payment amount does not match EMI");
            }

            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            Payment payment = client.payments.fetch(paymentId);
            Order order = client.orders.fetch(orderId);

            String expectedReceipt = "loan_" + dto.getLoanAccountId()
                    + "_emi_" + dto.getEmiScheduleId();

            long expectedPaise = requestedAmount
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            if (!orderId.equals(String.valueOf(payment.get("order_id")))
                    || !expectedReceipt.equals(String.valueOf(order.get("receipt")))
                    || !"INR".equalsIgnoreCase(
                    String.valueOf(payment.get("currency")))
                    || expectedPaise != Long.parseLong(
                    String.valueOf(payment.get("amount")))
                    || !"captured".equalsIgnoreCase(
                    String.valueOf(payment.get("status")))) {
                throw new IllegalArgumentException(
                        "Razorpay payment details could not be verified");
            }

            LoanPayments savedPayment = new LoanPayments();
            savedPayment.setLoanAccountId(dto.getLoanAccountId());
            savedPayment.setPaymentAmount(requestedAmount);
            savedPayment.setPaymentStatus(PaymentStatus.PAID);
            savedPayment.setPaymentName(paymentId);

            savedPayment = paymentsRepository.save(savedPayment);

            emi.setPaymentStatus(PaymentStatus.PAID);
            emi.setPaidDate(LocalDateTime.now());
            emiRepository.save(emi);

            dto.setPaymentId(savedPayment.getPaymentId());
            dto.setPaymentStatus(PaymentStatus.PAID);
            dto.setPaymentAmount(savedPayment.getPaymentAmount());
            dto.setPaymentName(paymentId);
            dto.setPaymentDate(LocalDateTime.now());

            return dto;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Payment verification failed: " + e.getMessage(), e);
        }
    }
}
