package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.user.ListUsersBanned;
import com.example.KaizenStream_BE.dto.respone.user.UserAccount;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<User> createUser(@RequestBody User user) {

        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable String id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted successfully!");
    }

    @GetMapping("/ban-list")
    public ApiResponse<List<ListUsersBanned>> getUsersBan(){
        List<ListUsersBanned> list = userService.getAllBannedUsers();
        return ApiResponse.<List<ListUsersBanned>>builder()
                .code(200)
                .message("Get list banned users successfully !")
                .result(list)
                .build();
    }
    @PutMapping("/unban/{userId}")
    public ApiResponse<UserAccount> unbanUser(@PathVariable String userId) {
        UserAccount user = userService.unbanUser(userId);
        return ApiResponse.<UserAccount>builder()
                .code(200)
                .message("Get list banned users successfully !")
                .result(user)
                .build();
    }
}
