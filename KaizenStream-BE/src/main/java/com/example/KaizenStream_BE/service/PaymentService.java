package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.entity.*;
import com.example.KaizenStream_BE.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

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

//    public void handlePaymentSuccess(String sessionId, String userId, double amount) {
//        // Tìm user theo userId
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found"));
//
//        // Tính số điểm: 10 USD = 100 điểm
//        // Lấy số tiền thanh toán từ tham số `amount` và tính số điểm tương ứng
//        int points = (int) (amount * 10); // Mỗi 10 USD = 100 điểm
//
//        // Tạo đối tượng Purchase và gán giá trị
//        Purchase purchase = new Purchase();
//        purchase.setUser(user); // Đảm bảo `user` không phải null
//        purchase.setAmount(amount); // Sử dụng số tiền thanh toán
//        purchase.setPointReceived(points); // Lưu số điểm vào bảng `purchases`
//        purchase.setPurchaseDate(LocalDateTime.now());  // Đảm bảo có ngày giao dịch
//
//        // Lưu thông tin vào bảng `purchases`
//        purchaseRepository.save(purchase); // Gọi phương thức `save` của `PurchaseRepository`
//
//        // Cập nhật số dư ví
//        Wallet wallet = walletRepository.findByUser(user).orElse(null);
//        if (wallet == null) {
//            wallet = new Wallet();
//            wallet.setUser(user);
//            wallet.setBalance(points); // Tạo mới ví và gán điểm vào
//        } else {
//            wallet.setBalance(wallet.getBalance() + points); // Cộng điểm vào ví
//        }
//
//        // Lưu cập nhật ví vào cơ sở dữ liệu
//        walletRepository.save(wallet); // Gọi phương thức `save` của `WalletRepository`
//    }

}
