package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.request.authen.AuthenticationRequest;
import com.example.KaizenStream_BE.dto.request.authen.IntrospectRequest;
import com.example.KaizenStream_BE.dto.respone.authen.AuthenticationResponse;
import com.example.KaizenStream_BE.dto.respone.authen.IntrospectRespone;
import com.example.KaizenStream_BE.entity.InvalidatedToken;
import com.example.KaizenStream_BE.entity.Permission;
import com.example.KaizenStream_BE.entity.Role;
import com.example.KaizenStream_BE.entity.User;
import com.example.KaizenStream_BE.enums.ErrorCode;
import com.example.KaizenStream_BE.exception.AppException;
import com.example.KaizenStream_BE.repository.InvalidatedRepository;
import com.example.KaizenStream_BE.repository.PermissionRepository;
import com.example.KaizenStream_BE.repository.RoleRepository;
import com.example.KaizenStream_BE.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Setter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService {
    @NonFinal
    @Value("${spring.jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${spring.jwt.validDuration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${spring.jwt.refreshableDuration}")
    protected long REFRESHABLE_DURATION;
    private static final Logger log = LogManager.getLogger(AuthenticationService.class);

    InvalidatedRepository invalidatedRepository;

    UserRepository userRepository;
    RoleRepository roleRepository;
    PermissionRepository permissionRepository;


    public IntrospectRespone introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();// Nhận token từ client ở đây
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectRespone.builder().valid(isValid).build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request, HttpServletResponse response) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Generate tokens
        var accessToken = generateToken(user, VALID_DURATION);
        var refreshToken = generateToken(user, REFRESHABLE_DURATION);

        // Set refresh token in cookie
        addRefreshTokenCookie(response, refreshToken);

        // Return minimal response without userName
        return AuthenticationResponse.builder()
                .token(accessToken)
                .userId(user.getUserId())
                .authenticated(true)
                .build();
    }

    public void logout(HttpServletResponse response) {
        // Chỉ set header một lần để xóa cookie
        response.setHeader("Set-Cookie", 
            "refreshToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=Strict; Domain=localhost");
    }

    public AuthenticationResponse refreshAccessToken(String refreshToken) {
        try {
            // Verify refresh token validity
            SignedJWT signedJWT = verifyToken(refreshToken, true);
            String userId = signedJWT.getJWTClaimsSet().getSubject();

            // Check if token is blacklisted
            String tokenId = signedJWT.getJWTClaimsSet().getJWTID();
            if (invalidatedRepository.existsById(tokenId)) {
                throw new AppException(ErrorCode.UNAUTHENTICATED);
            }

            // Get user and validate
            var user = userRepository.findById(userId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));

            // Generate new access token
            String newAccessToken = generateToken(user, VALID_DURATION);

            return AuthenticationResponse.builder()
                    .token(newAccessToken)
                    .userId(user.getUserId())
                    .authenticated(true)
                    .build();

        } catch (ParseException | JOSEException e) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        // Chỉ set header một lần với đầy đủ thông tin
        String headerValue = String.format("refreshToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=Strict; Domain=localhost", 
            refreshToken, REFRESHABLE_DURATION);
        response.setHeader("Set-Cookie", headerValue);
    }

    private SignedJWT verifyToken(String token, boolean isRefreshToken) throws ParseException, JOSEException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        
        // Verify signature
        if (!signedJWT.verify(verifier)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Verify expiration
        Date expirationTime = isRefreshToken ? 
            new Date(signedJWT.getJWTClaimsSet().getIssueTime().getTime() + REFRESHABLE_DURATION * 1000) :
            signedJWT.getJWTClaimsSet().getExpirationTime();

        if (expirationTime.before(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }

    private String generateToken(User user, long duration) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .issuer("katys")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(duration, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .build();
        
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(jwsHeader, payload);
        
        try {
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            log.error("Can't create token", e);
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        log.warn("buildScopebuildScopebuildScopebuildScopebuildScope");
        System.out.println("buildScopebuildScopebuildScopebuildScopebuildScope11");
        StringJoiner stringJoiner = new StringJoiner(" ");
        List<Role> roles = user.getRoles();

        if (!CollectionUtils.isEmpty(user.getRoles())) {
            roles.forEach(role -> {
                log.warn("ROLE\t\t: " + role.getName());
                System.out.println("ROLE11\t\t: " + role.getName());

                stringJoiner.add("ROLE_" + role.getName());
                if (!CollectionUtils.isEmpty(role.getPermissions()))
                    role.getPermissions()
                            .forEach(permission -> stringJoiner.add(permission.getName()));
            });
        }
        return stringJoiner.toString();
    }
}
