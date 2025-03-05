package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test,String> {
     boolean existsByName(String name);

}
