package com.example.KaizenStream_BE.service;
import com.example.KaizenStream_BE.entity.Role;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import com.example.KaizenStream_BE.entity.User;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserService userService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");

        User user;
        try {
            user = userService.findUserByEmail(email);
        } catch (RuntimeException e) {
            user = User.builder()
                    .email(email)
                    .userName(name)
                    .password("")
                    .roles(List.of(Role.USER))
                    .build();
            userService.save(user);
        }


        return oAuth2User;
    }

}
