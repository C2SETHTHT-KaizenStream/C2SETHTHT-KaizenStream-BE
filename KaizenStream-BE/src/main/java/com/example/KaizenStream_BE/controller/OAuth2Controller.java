package com.example.KaizenStream_BE.controller;

import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.service.AuthenticationService;
import com.example.KaizenStream_BE.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/auth/oauth2")
@RequiredArgsConstructor
public class OAuth2Controller {

    private final UserService userService;
    private final AuthenticationService authenticationService;

    @GetMapping("/success")
    public void oauth2Success(@AuthenticationPrincipal OAuth2User oauthUser, HttpServletResponse response) throws IOException {
        String email = oauthUser.getAttribute("email");
        if (email == null) {
            response.sendRedirect("http://localhost:3000/login?error=missing_email");
            return;
        }

        User user;
        try {
            user = userService.findUserByEmail(email);
        } catch (RuntimeException e) {
            response.sendRedirect("http://localhost:3000/login?error=user_not_found");
            return;
        }

        String token = authenticationService.generateToken(user);

        String redirectUrl = "http://localhost:3000/oauth2/redirect?token=" + token;
        response.sendRedirect(redirectUrl);
    }
}


