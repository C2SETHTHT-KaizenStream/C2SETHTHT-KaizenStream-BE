package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.WalletRepository;
import com.example.KaizenStream_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletRepository walletRepository;
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWalletByUserId(@PathVariable String userId) {
        User user = userService.getUserById(userId);

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("balance", wallet.getBalance());

        return ResponseEntity.ok(response);
    }
}
