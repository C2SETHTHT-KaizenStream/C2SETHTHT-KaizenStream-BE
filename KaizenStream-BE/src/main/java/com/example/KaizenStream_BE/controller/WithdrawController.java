package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.withdraw.WithdrawResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import com.example.KaizenStream_BE.entity.Withdraw;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.WalletRepository;
import com.example.KaizenStream_BE.service.UserService;
import com.example.KaizenStream_BE.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/withdraw")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService withdrawService;
    private final UserService userService;
    private final WalletRepository walletRepository;

    // Tạo yêu cầu rút tiền (User)
    @PostMapping
    public WithdrawResponse createWithdrawRequest(@RequestParam int pointsRequested,
                                                  @RequestParam String bankName,
                                                  @RequestParam String bankAccount) {
        // Lấy JWT token từ context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Lấy userId từ JWT claims
        String userId = null;
        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userId = jwt.getSubject(); // hoặc jwt.getClaim("userId")
        }

        if (userId == null) {
            throw new RuntimeException("User not authenticated properly");
        }

        // Sử dụng userService để lấy đối tượng User
        User user = userService.getUserById(userId);

        // Gọi withdraw service với user đã lấy được
        Withdraw withdraw = withdrawService.createWithdrawRequest(user, pointsRequested, bankName, bankAccount);

        // Trả về response như cũ, thêm thông tin balance
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // Trả về response như cũ
        return WithdrawResponse.builder()
                .withdrawId(withdraw.getWithdrawId())
                .userId(withdraw.getUser().getUserId())
                .pointsRequested(withdraw.getPointsRequested())
                .usdExpected(withdraw.getUsdExpected())
                .bankName(withdraw.getBankName())
                .bankAccount(withdraw.getBankAccount())
                .status(withdraw.getStatus())
                .note(withdraw.getNote())
                .createdAt(withdraw.getCreatedAt())
                .balance(wallet.getBalance())  // Trả về số dư ví của người dùng
                .build();
    }

    // Admin duyệt yêu cầu rút tiền
    @PutMapping("/{id}/approve")
    public WithdrawResponse approveWithdraw(@PathVariable String id) {
        try {
            System.out.println("Attempting to approve withdraw with ID: " + id);
            Withdraw withdraw = withdrawService.approveWithdraw(id);
            System.out.println("Withdraw approved successfully: " + withdraw);

            return WithdrawResponse.builder()
                    .withdrawId(withdraw.getWithdrawId())
                    .userId(withdraw.getUser().getUserId())
                    .pointsRequested(withdraw.getPointsRequested())
                    .usdExpected(withdraw.getUsdExpected())
                    .bankName(withdraw.getBankName())
                    .bankAccount(withdraw.getBankAccount())
                    .status(withdraw.getStatus())
                    .note(withdraw.getNote())
                    .createdAt(withdraw.getCreatedAt())
                    .build();
        } catch (Exception e) {
            System.err.println("Error approving withdraw: " + e.getMessage());
            throw e;
        }
    }

    // Admin từ chối yêu cầu rút tiền
    @PutMapping("/{id}/reject")
    public WithdrawResponse rejectWithdraw(@PathVariable String id, @RequestBody String note) {
        Withdraw withdraw = withdrawService.rejectWithdraw(id, note);

        // Sử dụng Builder để tạo WithdrawResponse
        return WithdrawResponse.builder()
                .withdrawId(withdraw.getWithdrawId())
                .userId(withdraw.getUser().getUserId())
                .pointsRequested(withdraw.getPointsRequested())
                .usdExpected(withdraw.getUsdExpected())
                .bankName(withdraw.getBankName())
                .bankAccount(withdraw.getBankAccount())
                .status(withdraw.getStatus())
                .note(withdraw.getNote())
                .createdAt(withdraw.getCreatedAt())
                .build();
    }

    // Lấy tất cả yêu cầu rút tiền (Admin)
    @GetMapping
    public List<WithdrawResponse> getAllWithdrawRequests() {
        List<Withdraw> withdraws = withdrawService.getAllWithdrawRequests();

        return withdraws.stream()
                .map(withdraw -> WithdrawResponse.builder()
                        .withdrawId(withdraw.getWithdrawId())
                        .userId(withdraw.getUser().getUserId())
                        .pointsRequested(withdraw.getPointsRequested())
                        .usdExpected(withdraw.getUsdExpected())
                        .bankName(withdraw.getBankName())
                        .bankAccount(withdraw.getBankAccount())
                        .status(withdraw.getStatus())
                        .note(withdraw.getNote())
                        .createdAt(withdraw.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<Map<String, Object>> getUserBalance(@PathVariable String userId) {
        User user = userService.getUserById(userId);

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("balance", wallet.getBalance());

        return ResponseEntity.ok(response);
    }

    // Endpoint để lấy lịch sử rút tiền của một user
    @GetMapping("/withdraw-history/{userId}")
    public ApiResponse<List<WithdrawResponse>> getWithdrawHistory(@PathVariable String userId) {
        // Gọi service để lấy lịch sử rút tiền
        return withdrawService.getWithdrawHistory(userId);
    }
}
