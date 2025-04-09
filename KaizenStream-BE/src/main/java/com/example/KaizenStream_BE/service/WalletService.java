package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.wallet.WalletBalanceResponse;
import com.example.KaizenStream_BE.entity.Wallet;
import com.example.KaizenStream_BE.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletBalanceResponse getWalletBalance(String userId) {
        Optional<Wallet> wallet = walletRepository.findByUser_UserId(userId);
        if (wallet.isPresent()) {
            return WalletBalanceResponse.builder()
                    .balance(wallet.get().getBalance())
                    .build();
        }
        return WalletBalanceResponse.builder()
                .balance(0)  // Default value if wallet is not found
                .build();
    }

}
