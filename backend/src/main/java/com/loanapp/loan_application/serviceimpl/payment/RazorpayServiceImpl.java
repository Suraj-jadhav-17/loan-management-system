package com.loanapp.loan_application.serviceimpl.payment;

import com.loanapp.loan_application.dto.payment.LoanPaymentsDto;
import com.loanapp.loan_application.service.payment.LoanPaymentsService;
import com.loanapp.loan_application.service.payment.RazorpayService;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RazorpayServiceImpl implements RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;



    @Override
    public String createOrder(BigDecimal amount, Long loanAccountId, Long emiScheduleId) {
        try {RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderRequest = new JSONObject();
            long amountInPaise = amount
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "loan_" + loanAccountId + "_emi_" + emiScheduleId);
            Order order = client.orders.create(orderRequest);
            return order.get("id");

        } catch (Exception e) {
            throw new RuntimeException("Unable to create Razorpay order", e
            );
        }
    }

    @Override
    public boolean verifySignature(String orderId, String paymentId, String signature) {
        try {
            String data = orderId + "|" + paymentId;
            return Utils.verifySignature(data, signature, keySecret);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Payment verification failed", e
            );
        }
    }


    @Override
    public boolean verifyForeclosurePayment(String orderId, String paymentId, String signature, BigDecimal expectedAmount) {
        try {
            if (orderId == null || paymentId == null || signature == null
                    || expectedAmount == null
                    || expectedAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return false;
            }
            if (!verifySignature(orderId, paymentId, signature)) {
                return false;
            }

            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            com.razorpay.Payment payment = client.payments.fetch(paymentId);
            long expectedPaise = expectedAmount.multiply(BigDecimal.valueOf(100)).longValueExact();

            return orderId.equals(payment.get("order_id"))
                    && expectedPaise == ((Number) payment.get("amount")).longValue()
                    && "captured".equalsIgnoreCase(String.valueOf(payment.get("status")));
        } catch (Exception e) {
            throw new RuntimeException("Foreclosure payment verification failed", e);
        }
    }


    @Override
    public LoanPaymentsDto verifyPayment(String orderId, String paymentId, String signature, LoanPaymentsDto dto) {
        return null;
    }
}