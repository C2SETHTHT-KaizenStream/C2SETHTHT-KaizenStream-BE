package com.example.KaizenStream_BE.service;


import com.example.KaizenStream_BE.dto.request.authen.AuthenticationRequest;
import com.example.KaizenStream_BE.dto.request.authen.IntrospectRequest;
import com.example.KaizenStream_BE.dto.respone.authen.AuthenticationResponse;
import com.example.KaizenStream_BE.dto.respone.authen.IntrospectRespone;
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
        var token = request.getToken();
        boolean isValid = true;
        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectRespone.builder().valid(isValid).build();

    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // Sử dụng BCryptPasswordEncoder để kiểm tra mật khẩu
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        // Tìm người dùng trong cơ sở dữ liệu
        var user = userRepository.findByUserName(request.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXIST));
        // Kiểm tra mật khẩu đã mã hóa trong cơ sở dữ liệu với mật khẩu người dùng nhập vào
        boolean authenticated = passwordEncoder.matches(request.getPassword(), user.getPassword());

        // Nếu mật khẩu không khớp, ném lỗi UNAUTHENTICATED
        if (!authenticated) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Tạo JWT token cho người dùng nếu xác thực thành công
        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)           // Trả về token
                .authenticated(true)    // Đánh dấu đăng nhập thành công
                .userId(user.getUserId())  // Trả về userId của người dùng
                .build();
    }


    private SignedJWT verifyToken(String token, Boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);
        Date expityTime = isRefresh ?
                new Date(signedJWT.getJWTClaimsSet().getIssueTime().toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();
        var verified = signedJWT.verify((verifier));
        log.warn("54555555555555");

        if (!verified && expityTime.after(new Date())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);

        }
        //log.warn("aaaaaaaaaaaaaaaaaaaaaaaaas");
        if (invalidatedRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID()))
            throw new AppException(ErrorCode.UNAUTHORIZED);
        //log.warn("99999999999");

        return signedJWT;
    }

    //    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
//        var signedJWT=verifyToken(request.getToken(),true);
//        String jti=signedJWT.getJWTClaimsSet().getJWTID(); // jwt id
//
//        Date expiryTime=signedJWT.getJWTClaimsSet().getExpirationTime();
//        InvalidatedToken invalidatedToken=InvalidatedToken.builder()
//                .id(jti)
//                .expiryTime(expiryTime)
//                .build();
//        //log.info("invalidatedToken\t\t\t"+invalidatedToken);
//        invalidatedRepository.save(invalidatedToken);
//        var userName=signedJWT.getJWTClaimsSet().getSubject();
//        var user=userRepository.findByUserName(userName).orElseThrow(
//                ()->new AppException(ErrorCode.UNAUTHENTICATED)
//        );
//        var token=generateToken(user);
//        log.warn("TOKENN"+token);
//        return  AuthenticationResponse.builder().token(token).authenticated(true).build();
//
//    }
    private String generateToken(User user) {
        JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserName())
                .issuer("katys")
                .issueTime(new Date()).expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.SECONDS).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
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
