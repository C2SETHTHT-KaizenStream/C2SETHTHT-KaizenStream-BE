package com.example.KaizenStream_BE.configuration;

import com.example.KaizenStream_BE.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final String[] PUBLIC_ENDPOINTS = {

//            "/auth/**",
//            //"/blogs/**",
////            "/users/**",
//            "/comments/**",
//            "/ws/**",
//            "/topic/notifications",
//            "/api/stream/ws/info",
//            "/item/**",
//            "/livestream/**",
//            "/category/**",
//            "/topic/**",
//            "/chat/**",
//            "/donation/**",
//            "/schedule/**",
//            "/tag/**",
//            "/payment/**",
//            "/report/**",
//            "/leaderboard/**",
//            "/notification/**",
//            "/search/**",
////            "/follow/**",
//            "/withdraw",
//            "/chart/**"

            "/auth/**",
           //"/blogs/**",
            "/users/**",
            "/comments/**",
//            "*",
            "/ws",
            "/ws/**",
//            "/ws/*",
            "/topic/notifications",
            "/api/stream/ws/info",
            "/item/*",
            "/item/update/**",
            "/item/update/*",
            "/api/stream/ws/info",
            "/api/stream/**",
            "/profile/**",
            "/livestream/**",
            "/category/**",
            "/topic/**",
            "/chat/**",
            "/donation/**",
            "/schedule/**",
            "/tag/**",
            "/payment/**",
            "/report/**",
            "/leaderboard/**",
            "/report/**",
            "/notification/**",
            "/users/**",
            "/search/**",
            //"follow/**",
            "/withdraw",
            "/chart/**",
//            "/chart/**"
    };

    @Value("${fe-url}")
    protected String feUrl;

    private final CustomJwtDecoder customJwtDecoder;
    private final CustomOAuth2UserService customOAuth2UserService;

    public SecurityConfig(CustomJwtDecoder customJwtDecoder, CustomOAuth2UserService customOAuth2UserService) {
        this.customJwtDecoder = customJwtDecoder;
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        .anyRequest().authenticated()
                )
//                .oauth2Login(oauth2 -> oauth2
//                        .defaultSuccessUrl("http://localhost:8080/auth/oauth2/success", true)
//                        .failureUrl("http://localhost:3000/login?error=true")
//                        .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
//                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(customJwtDecoder)
                                .jwtAuthenticationConverter(jwtAuthenticationConverter()))
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of(feUrl));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
