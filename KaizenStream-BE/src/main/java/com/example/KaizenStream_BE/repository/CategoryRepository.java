package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository  extends JpaRepository<Category,String> {

    Optional<Category> findByName(String name);
    Optional<Category> findFirstByNameIgnoreCase(String name);


}
