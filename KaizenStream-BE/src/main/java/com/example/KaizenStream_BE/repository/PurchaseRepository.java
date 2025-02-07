package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Chat;
import com.example.KaizenStream_BE.entity.Purchase;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository
public interface PurchaseRepository extends Repository<Purchase,String> {
}
