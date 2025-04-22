package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.user.ListUsersBanned;
import com.example.KaizenStream_BE.dto.respone.user.UserAccount;
import com.example.KaizenStream_BE.entity.Role;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.AccountStatus;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

    public User createUser(User user) {
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new RuntimeException("User already exists!");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        List<Role> roles = new ArrayList<>();
        roles.add(Role.USER);
        roles.add(Role.ADMIN);
        user.setRoles(roles);


        return userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public void deleteUser(String userId) {
        userRepository.deleteById(userId);
    }

    // email
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found!"));
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public List<ListUsersBanned> getAllBannedUsers() {
        // Lấy tất cả người dùng từ cơ sở dữ liệu có trạng thái BANNED
        List<User> bannedUsers = userRepository.findByStatus(AccountStatus.BANNED);

        // Chuyển đổi danh sách User thành ListUsersBanned DTO
        return bannedUsers.stream()
                .map(user -> new ListUsersBanned(
                        user.getUserId(),
                        user.getUserName(),
                        user.getEmail(),
                        user.getStatus(),
                        user.getBanUntil()
                ))
                .collect(Collectors.toList());
    }

    public UserAccount unbanUser(String userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        user.setStatus(AccountStatus.ACTIVE);
        user.setBanUntil(null);
        userRepository.save(user);
        UserAccount userAccount = new UserAccount();
        userAccount.setStatus(user.getStatus());
        userAccount.setUserId(user.getUserId());
        return userAccount;
    }
}
