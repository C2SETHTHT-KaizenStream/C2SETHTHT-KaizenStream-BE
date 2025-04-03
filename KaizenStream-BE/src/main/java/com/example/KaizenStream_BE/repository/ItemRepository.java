package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Item;
import com.example.KaizenStream_BE.enums.StatusItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, String> {
    // Tìm kiếm danh sách Item theo status
    List<Item> findByStatus(StatusItem status);
}
