package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.Donation;
import com.example.KaizenStream_BE.entity.Livestream;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface DonationRepository extends JpaRepository<Donation,String> {
    // Tìm danh sách donation theo user (người tặng)
    List<Donation> findByUser(User user);

    // Tìm danh sách donation theo livestream
    List<Donation> findByLivestream(Livestream livestream);

    // Tìm tất cả donation của một người dùng trong một livestream cụ thể
    List<Donation> findByUserAndLivestream(User user, Livestream livestream);
}
