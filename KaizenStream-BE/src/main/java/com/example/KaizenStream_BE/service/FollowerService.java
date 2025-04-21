package com.example.KaizenStream_BE.service;

import com.example.KaizenStream_BE.dto.respone.profile.FollowRespone;
import com.example.KaizenStream_BE.entity.Follower;
import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.FollowerRepository;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public class FollowerService {

    FollowerRepository followerRepository;
     UserRepository userRepository;
     ProfileRepository profileRepository;

    public void follow(String followerId, String followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if (followerRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new AppException(ErrorCode.ALREADY_FOLLOWING);
        }

        Follower f = Follower.builder()
                .follower(follower)
                .following(following)
                .followedAt(LocalDateTime.now())
                .build();
        followerRepository.save(f);

        following.setFollowerCount(following.getFollowerCount() + 1);
        userRepository.save(following);

//        follower.setFlowingCount(follower.getFlowingCount() + 1);
//        userRepository.save(follower);
    }

    public void unfollow(String followerId, String followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        followerRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public List<FollowRespone> getFollowers(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<Follower> followers = followerRepository.findByFollowing(user);

        return followers.stream().map(follower -> {
            User followerUser = follower.getFollower();

            Profile profile = profileRepository.findByUser_UserId(followerUser.getUserId())
                    .orElse(null);
            FollowRespone f= new FollowRespone();
            f.setUserId(followerUser.getUserId());
            f.setUserName(followerUser.getUserName());
            f.setAvaUrl(profile != null ? profile.getAvatarUrl() : null);
            f.setFollowedAt(follower.getFollowedAt());
            return f;
        }).toList();
    }


    public List<FollowRespone> getFollowing(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        List<Follower> followingList = followerRepository.findByFollower(user);

        return followingList.stream().map(follower -> {
            User followingUser = follower.getFollowing();

            Profile profile = profileRepository.findByUser_UserId(followingUser.getUserId())
                    .orElse(null);

            FollowRespone f = new FollowRespone();
            f.setUserId(followingUser.getUserId());
            f.setUserName(followingUser.getUserName());
            f.setAvaUrl(profile != null ? profile.getAvatarUrl() : null);
            f.setFollowedAt(follower.getFollowedAt());
            return f;
        }).toList();
    }

}
