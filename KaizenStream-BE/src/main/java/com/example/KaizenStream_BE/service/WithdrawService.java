package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.ApiResponse;
import com.example.KaizenStream_BE.dto.respone.withdraw.WithdrawResponse;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.entity.Wallet;
import com.example.KaizenStream_BE.entity.Withdraw;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.enums.WithdrawStatus;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.UserRepository;
import com.example.KaizenStream_BE.repository.WithdrawRepository;
import com.example.KaizenStream_BE.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WithdrawService {

    private final WithdrawRepository withdrawRepo;
    private final WalletRepository walletRepo;
     private final UserService userService;  // Thêm service để lấy thông tin user
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final WithdrawRepository withdrawRepository;

    // Tạo yêu cầu rút tiền
    public Withdraw createWithdrawRequest(User user, int pointsRequested, String bankName, String bankAccount) {
        // Kiểm tra đủ số điểm yêu cầu tối thiểu (5000 Points)
        if (pointsRequested < 5000) {
            throw new AppException(ErrorCode.MINIMUM_WITHDRAW_POINTS);
        }

        // Lấy thông tin ví của người dùng
        Wallet wallet = walletRepo.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // Kiểm tra nếu user có đủ số điểm
        if (wallet.getBalance() < pointsRequested) {
            throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
        }

        // Tính số tiền USD mà người dùng sẽ nhận
        double usdExpected = (pointsRequested / 100.0) * 0.7;

        // Lưu yêu cầu vào bảng withdraws
        Withdraw withdraw = Withdraw.builder()
                .user(user)
                .pointsRequested(pointsRequested)
                .usdExpected(usdExpected)
                .bankName(bankName)
                .bankAccount(bankAccount)
                .status(WithdrawStatus.PENDING)
                .build();

        return withdrawRepo.save(withdraw);
    }

    // Duyệt yêu cầu rút tiền
    public Withdraw approveWithdraw(String withdrawId) {
        // Lấy JWT token từ context và trích xuất userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userId = jwt.getSubject(); // Lấy userId từ JWT token
        }

        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        // Lấy thông tin người dùng từ service
        User currentUser = userService.getUserById(userId);

        // Tìm yêu cầu rút tiền
        Withdraw withdraw = withdrawRepo.findById(withdrawId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAW_REQUEST_NOT_FOUND));

        // Kiểm tra nếu yêu cầu đã được xử lý
        if (withdraw.getStatus() != WithdrawStatus.PENDING) {
            throw new AppException(ErrorCode.ALREADY_PROCESSED);
        }

        // Cập nhật trạng thái và thời gian updatedAt
        withdraw.setStatus(WithdrawStatus.APPROVED);
        withdraw.setUpdatedAt(LocalDateTime.now());  // Cập nhật thời gian

        // Lấy ví của người dùng
        Wallet wallet = walletRepo.findByUser(withdraw.getUser())
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_FOUND));

        // Kiểm tra nếu ví có đủ điểm
        if (wallet.getBalance() < withdraw.getPointsRequested()) {
            throw new AppException(ErrorCode.INSUFFICIENT_POINTS);
        }

        // Trừ số điểm từ ví của người dùng
        wallet.setBalance(wallet.getBalance() - withdraw.getPointsRequested());
        withdraw.setStatus(WithdrawStatus.APPROVED);

        // Lưu lại ví và cập nhật yêu cầu rút tiền
        walletRepo.save(wallet);
        return withdrawRepo.save(withdraw);
    }

    // Từ chối yêu cầu rút tiền
    public Withdraw rejectWithdraw(String withdrawId, String note) {
        // Lấy JWT token từ context và trích xuất userId
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = null;

        if (authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            userId = jwt.getSubject(); // Lấy userId từ JWT token
        }

        if (userId == null) {
            throw new AppException(ErrorCode.USER_NOT_EXIST);
        }

        // Lấy thông tin người dùng từ service
        User currentUser = userService.getUserById(userId);

        // Tìm yêu cầu rút tiền
        Withdraw withdraw = withdrawRepo.findById(withdrawId)
                .orElseThrow(() -> new AppException(ErrorCode.WITHDRAW_REQUEST_NOT_FOUND));

        // Kiểm tra nếu yêu cầu đã được xử lý
        if (withdraw.getStatus() != WithdrawStatus.PENDING) {
            throw new AppException(ErrorCode.ALREADY_PROCESSED);
        }

        withdraw.setStatus(WithdrawStatus.REJECTED);
        withdraw.setNote(note);
        withdraw.setUpdatedAt(LocalDateTime.now());  // Cập nhật thời gian

        // Cập nhật trạng thái từ chối
        return withdrawRepo.save(withdraw);
    }

    // Lấy tất cả yêu cầu rút tiền
    public List<Withdraw> getAllWithdrawRequests() {
        return withdrawRepo.findAll();
    }

    public ApiResponse<List<WithdrawResponse>> getWithdrawHistory(String userId) {
        // Tìm user theo userId
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        // Lấy wallet của user để xem số dư
        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new AppException(ErrorCode.WALLET_NOT_EXIST));

        // Lấy danh sách các withdraw của user, sắp xếp theo thời gian mới nhất
        List<Withdraw> withdraws = withdrawRepository.findByUserOrderByCreatedAtDesc(user);

        // Chuyển đổi từ Withdraw sang WithdrawResponse
        List<WithdrawResponse> withdrawResponses = withdraws.stream()
                .map(withdraw -> {
                    return WithdrawResponse.builder()
                            .withdrawId(withdraw.getWithdrawId())
                            .userId(user.getUserId())
                            .pointsRequested(withdraw.getPointsRequested())
                            .usdExpected(withdraw.getUsdExpected())
                            .bankName(withdraw.getBankName())
                            .bankAccount(withdraw.getBankAccount())
                            .status(withdraw.getStatus())
                            .note(withdraw.getNote())
                            .createdAt(withdraw.getCreatedAt())
                            .updatedAt(withdraw.getUpdatedAt())  // Thêm updatedAt
                            .balance(wallet.getBalance())  // Lấy số dư trong ví của người dùng
                            .build();
                })
                .collect(Collectors.toList());

        return ApiResponse.<List<WithdrawResponse>>builder()
                .message("Withdraw history retrieved successfully")
                .result(withdrawResponses)
                .build();
    }

}
