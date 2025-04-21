package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.purchase.PurchaseResponse;
import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentService {


    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;
    private final WalletRepository walletRepository;

    public void handlePaymentSuccess(String sessionId, String userId, double amount, String type) {
        // Tìm user theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Tính số điểm: 1 USD = 100 điểm
        // Lấy số tiền thanh toán từ tham số `amount` và tính số điểm tương ứng
        int points = (int) (amount * 100);

        // Tạo đối tượng Purchase và gán giá trị
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setAmount(amount);
        purchase.setType(type);
        purchase.setPointReceived(points);
        purchase.setPurchaseDate(LocalDateTime.now());

        // Lưu thông tin vào bảng `purchases`
        purchaseRepository.save(purchase);

        // Cập nhật số dư ví
        Wallet wallet = walletRepository.findByUser(user).orElse(null);
        if (wallet == null) {
            wallet = new Wallet();
            wallet.setUser(user);
            wallet.setBalance(points); // Tạo mới ví và gán điểm vào
        } else {
            wallet.setBalance(wallet.getBalance() + points); // Cộng điểm vào ví
        }

        // Lưu cập nhật ví vào cơ sở dữ liệu
        walletRepository.save(wallet); // Gọi phương thức `save` của `WalletRepository`
    }

    // Phương thức để lấy lịch sử mua điểm của một user
    public ApiResponse<List<PurchaseResponse>> getPurchaseHistory(String userId) {
        // Tìm user theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        
        // Lấy wallet của user để xem số dư
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXIST));
        
        // Lấy danh sách các purchase của user, sắp xếp theo thời gian mới nhất
        List<Purchase> purchases = purchaseRepository.findByUserOrderByPurchaseDateDesc(user);
        
        // Chuyển đổi từ Purchase sang PurchaseResponse
        List<PurchaseResponse> purchaseResponses = purchases.stream()
                .map(purchase -> {
                    PurchaseResponse response = new PurchaseResponse();
                    response.setUserId(user.getUserId());
                    response.setUserName(user.getUserName());
                    response.setPurchaseId(purchase.getPurchaseId());
                    response.setType(purchase.getType());
                    response.setAmount(purchase.getAmount());
                    response.setPurchaseDate(purchase.getPurchaseDate());
                    response.setPointReceived(purchase.getPointReceived());
                    response.setBalance(wallet.getBalance());
                    return response;
                })
                .collect(Collectors.toList());
        
        return ApiResponse.<List<PurchaseResponse>>builder()
                .message("Purchase history retrieved successfully")
                .result(purchaseResponses)
                .build();
    }

}
