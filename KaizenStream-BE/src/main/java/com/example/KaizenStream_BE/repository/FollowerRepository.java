package com.example.KaizenStream_BE.repository;

import com.example.KaizenStream_BE.entity.Comment;
import com.example.KaizenStream_BE.entity.Follower;
import com.example.KaizenStream_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.Repository;

import java.util.List;

@org.springframework.stereotype.Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {
    boolean existsByFollowerAndFollowing(User follower, User following);
    void deleteByFollowerAndFollowing(User follower, User following);
    List<Follower> findByFollower(User follower);
    List<Follower> findByFollowing(User following);
}