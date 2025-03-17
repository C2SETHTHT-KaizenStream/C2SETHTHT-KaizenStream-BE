package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.request.authen.RegisterRequest;
import com.example.KaizenStream_BE.entity.Role;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.mapper.UserMapper;
import com.example.KaizenStream_BE.repository.RoleRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
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
    RoleRepository roleRepository;
    @Autowired
    UserMapper userMapper;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Transactional
    public RegisterRequest register(RegisterRequest registerRequest) {
        Role role = roleRepository.findByName(registerRequest.getRole_name());
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

        return registerRequest;
    }

}
