package com.example.KaizenStream_BE.service.init;

import com.example.KaizenStream_BE.entity.Profile;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.repository.ProfileRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class InitProfileService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public int initProfileForAllUser() {
        List<User> users = userRepository.findAll();
        int count = 0;

        for (User user : users) {
            if (!profileRepository.existsByUser_UserId(user.getUserId())) {

                Profile profile = Profile.builder()
                        .user(user)
                        .fullName("User " + user.getUserName())
                        .phoneNumber("09" + new Random().nextInt(90000000, 99999999))
                        .bio("Welcome to KaizenStream!")
                        .avatarUrl("https://api.dicebear.com/9.x/adventurer/svg?seed=Amaya" + user.getUserName())
                        .address("Hanoi")
                        .gender("Unknown")
                        .build();

                profileRepository.save(profile);
                count++;
            }
        }

        return count;
    }
}

