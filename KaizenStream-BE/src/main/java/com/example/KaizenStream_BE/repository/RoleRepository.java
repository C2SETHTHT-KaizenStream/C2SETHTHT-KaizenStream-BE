package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository

public interface RoleRepository extends JpaRepository<Role,String> {
    public Role findByName(String name);
}
