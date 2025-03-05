package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Permission;
import org.springframework.data.repository.Repository;

@org.springframework.stereotype.Repository

public interface PermissionRepository extends Repository<Permission,String> {
}
