package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Purchase;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Repository
public interface PurchaseRepository extends Repository<Purchase,String> {
    // Lưu đối tượng Purchase vào cơ sở dữ liệu
    Purchase save(Purchase purchase);

    // Tìm một Purchase theo ID
    Optional<Purchase> findById(String id);

    // Lấy tất cả các Purchase
    Iterable<Purchase> findAll();
    
    // Tìm tất cả Purchase của một user và sắp xếp theo ngày mua giảm dần (mới nhất lên đầu)
    List<Purchase> findByUserOrderByPurchaseDateDesc(User user);

    // Xóa Purchase theo ID
    void deleteById(String id);
}
