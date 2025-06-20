package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.request.purchase.PurchaseRequest;
import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.StripeResponse;
import com.example.KaizenStream_BE.dto.respone.purchase.PurchaseResponse;
//import com.example.KaizenStream_BE.service.PaymentService;
import com.example.KaizenStream_BE.service.PaymentService;
import com.example.KaizenStream_BE.service.StripeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PaymentController {

    private final StripeService stripeService;
    private final PaymentService paymentService;

//    @PostMapping("/checkout")
//    public ResponseEntity<StripeRespone> checkout(@RequestBody PurchaseRequest request) {
//        StripeRespone response = stripeService.checkoutPurchase(request);
//        return ResponseEntity.status(HttpStatus.OK).body(response);
//    }

    // Endpoint để thanh toán (tạo session Stripe)
    @PostMapping("/checkout")
    public ResponseEntity<StripeResponse> checkout(@RequestBody PurchaseRequest purchaseRequest) {
        // Gọi service để tạo session Stripe và trả về StripeResponse
        StripeResponse stripeResponse = stripeService.checkoutPurchase(purchaseRequest);

        // Trả về ResponseEntity với mã trạng thái OK và đối tượng StripeResponse
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(stripeResponse);
    }

    // Endpoint để xử lý thanh toán thành công
    @GetMapping("/payment-success")
    public ResponseEntity<String> handlePaymentSuccess(
            @RequestParam String sessionId,
            @RequestParam String userId,
            @RequestParam double amount,
            @RequestParam String type
    ) {
        // Sau khi thanh toán thành công, xử lý thông tin người dùng và lưu vào database
        // Gọi service để xử lý thanh toán, cộng điểm vào wallet và lưu lịch sử
        // Lưu purchase vào database và cập nhật wallet balance
        paymentService.handlePaymentSuccess(sessionId, userId, amount, type);
        return ResponseEntity.ok("Payment successful for user: " + userId);
    }
    
    // Endpoint để lấy lịch sử mua điểm của một user
    @GetMapping("/history/{userId}")
    public ApiResponse<List<PurchaseResponse>> getPurchaseHistory(@PathVariable String userId) {
        return paymentService.getPurchaseHistory(userId);
    }
}

//package com.example.KaizenStream_BE.controller;
//
//import com.example.KaizenStream_BE.dto.request.purchase.PurchaseRequest;
//import com.example.KaizenStream_BE.dto.respone.StripeRespone;
//import com.example.KaizenStream_BE.service.PaymentService;
//import com.example.KaizenStream_BE.service.StripeService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//public class PaymentController {
//
//    @Autowired
//    private StripeService stripeService;
//
//    @Autowired
//    private PaymentService paymentService;
//
//    @PostMapping("/checkout")
//    public StripeRespone checkout(@RequestBody PurchaseRequest request) {
//        // Gọi service để tạo session Stripe và trả về StripeResponse
//        return stripeService.checkoutPurchase(request);
//    }
//
//    @GetMapping("/payment-success")
//    public String handlePaymentSuccess(@RequestParam String sessionId, @RequestParam String userId, @RequestParam double amount) {
//        // Sau khi thanh toán thành công, xử lý thông tin người dùng và lưu vào database
//        paymentService.handlePaymentSuccess(sessionId, userId, amount); // Xử lý thanh toán thành công
//        return "Payment successful for user: " + userId;
//    }
//}
