package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.dto.respone.authen.RegisterResponse;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.Role;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.UserMapper;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.RoleRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import com.example.KaizenStream_BE.repository.WalletRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class RegisterService {
    @Autowired
    WalletRepository walletRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ProfileRepository profileRepository;

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {
        Role role = new Role("1","USER" );
        if (role == null) {
            throw new AppException(ErrorCode.INVALID_ROLE);
        }

        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUserName(registerRequest.getUserName())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        user.setRoles(Collections.singletonList(role));

        userRepository.save(user);

        //Tạo Wallet ngay sau khi đăng ký user
        Wallet wallet = Wallet.builder()
                .user(user)
                .balance(0) // Số dư ban đầu
                .build();
        // Tạo Profile cho User ngay sau khi đăng ký
        Profile profile = Profile.builder()
                .user(user)
                .fullName(registerRequest.getUserName())  // Có thể để trống hoặc lấy từ registerRequest nếu có
                .phoneNumber("")
                .address("")
                .bio("")
                .avatarUrl("")
                .gender("")
                .dateOfBirth(null)
                .bankAccountNumber("")
                .bankName("")
                .description("")
                .build();
        profileRepository.save(profile);
        walletRepository.save(wallet);
        return RegisterResponse.builder().userName(user.getUserName())
                .userId(user.getUserId()).build();
    }

}
