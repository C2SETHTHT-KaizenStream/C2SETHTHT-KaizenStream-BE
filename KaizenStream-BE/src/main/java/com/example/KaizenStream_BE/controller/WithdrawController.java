package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.withdraw.WithdrawResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Withdraw;
import com.example.KaizenStream_BE.service.WithdrawService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/withdraws")
@RequiredArgsConstructor
public class WithdrawController {

    private final WithdrawService withdrawService;

    // Tạo yêu cầu rút tiền (User)
    @PostMapping
    public WithdrawResponse createWithdrawRequest(@RequestParam int pointsRequested,
                                                  @RequestParam String bankName,
                                                  @RequestParam String bankAccount) {
        // Lấy người dùng từ context (Spring Security)
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Withdraw withdraw = withdrawService.createWithdrawRequest(user, pointsRequested, bankName, bankAccount);

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

    // Admin duyệt yêu cầu rút tiền
    @PutMapping("/{id}/approve")
    public WithdrawResponse approveWithdraw(@PathVariable String id) {
        Withdraw withdraw = withdrawService.approveWithdraw(id);

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
}
