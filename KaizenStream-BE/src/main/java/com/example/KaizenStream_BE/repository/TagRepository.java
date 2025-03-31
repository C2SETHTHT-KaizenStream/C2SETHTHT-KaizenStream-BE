package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Category;
import com.example.KaizenStream_BE.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository  extends JpaRepository<Tag,String> {
    Tag findByName(String name);
    Optional<Tag> findFirstByNameIgnoreCase(String name);


}
