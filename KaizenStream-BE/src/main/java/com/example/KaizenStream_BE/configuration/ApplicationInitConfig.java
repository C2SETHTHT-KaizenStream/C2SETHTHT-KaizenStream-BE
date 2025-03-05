package com.example.KaizenStream_BE.configuration;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.Role;
import com.example.KaizenStream_BE.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class ApplicationInitConfig {
    private static final Logger log = LoggerFactory.getLogger(ApplicationInitConfig.class);

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository){
        return  args -> {

//            if(userRepository.findByUserName("admin123").isEmpty()){
//                HashSet<String> roles=new HashSet<>();
//                roles.add(Role.ADMIN.name());
//                User user= User.builder().userName("admin123").password(("admin123"))
//                        // .roles(roles)
//                        .build();
//                userRepository.save(user);
//                log.warn("admin user has been created with default password: admin, please change it");
//            }
        };
    }
}
