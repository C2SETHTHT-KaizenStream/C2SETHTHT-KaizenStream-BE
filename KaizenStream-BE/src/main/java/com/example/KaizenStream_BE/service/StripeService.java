package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.purchase.PurchaseRequest;
import com.example.KaizenStream_BE.dto.respone.StripeRespone;
import com.stripe.*;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.StripeResponse;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeService {
    // Inject giá trị từ .env
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;
    @Value("${FE_URL}")
    private String frontendUrl;


    public StripeRespone checkoutPurchase(PurchaseRequest request) {
        Stripe.apiKey = stripeSecretKey;

        // Stripe yêu cầu số tiền phải là cent (100 = 1 USD)
        long unitAmount = (long) (request.getAmount() * 100);

        SessionCreateParams.LineItem.PriceData.ProductData productData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(request.getType()) // Sử dụng `type` làm mô tả cho thanh toán
                        .build();

        // Cấu hình PriceData cho Stripe
        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency("USD")  // Đặt đơn vị tiền tệ
                        .setUnitAmount(unitAmount)  // Sử dụng số tiền đã chuyển sang cents
                        .setProductData(productData)
                        .build();

        // Tạo một LineItem cho Stripe Session
        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)  // Dùng số lượng 1, vì đây là thanh toán 1 lần
                        .setPriceData(priceData)
                        .build();

        // Tạo parameters cho session thanh toán
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)  // Chế độ thanh toán
                .setSuccessUrl(frontendUrl + "/payment-success?sessionId={CHECKOUT_SESSION_ID}&userId=" + request.getUserId())  // Điều hướng sau khi thanh toán thành công
                .setCancelUrl(frontendUrl + "/payment-cancel")  // Điều hướng khi thanh toán bị hủy
                .addLineItem(lineItem)  // Thêm line item vào session
                .build();


        try {
            // Tạo một session thanh toán
            Session session = Session.create(params);

            // Trả về thông tin session sau khi tạo thành công
            return StripeRespone
                    .builder()
                    .status("SUCCESS")
                    .message("Payment session created")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
        } catch (StripeException e) {
            // Ném lỗi nếu Stripe gặp sự cố
            throw new RuntimeException("Stripe error: " + e.getMessage());
        }
    }

}
