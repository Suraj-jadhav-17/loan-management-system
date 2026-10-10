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
}