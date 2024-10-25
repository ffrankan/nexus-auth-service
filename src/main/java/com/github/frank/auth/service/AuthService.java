package com.github.frank.auth.service;

import com.github.frank.auth.dto.LoginRequest;
import com.github.frank.auth.dto.LoginResponse;
import com.github.frank.auth.dto.RegisterRequest;
import com.github.frank.auth.dto.UserInfo;
import com.github.frank.common.exception.BusinessException;
import com.github.frank.common.exception.ErrorCode;
import com.github.frank.system.entity.User;
import com.github.frank.system.repository.RoleRepository;
import com.github.frank.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Frank An
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        try {
            // 1. 先尝试认证
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.username(),
                            request.password()
                    )
            );

            // 2. 认证成功后获取用户信息
            var user = userRepository.findByUsername(request.username())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

            var token = jwtService.generateToken(user);

            log.debug("User {} successfully logged in", request.username());
            return new LoginResponse(token, UserInfo.fromUser(user));

        } catch (AuthenticationException e) {
            log.warn("Failed to authenticate user: {}", request.username());
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }
    }

    @Transactional
    public UserInfo register(RegisterRequest request) {
        // 检查用户名是否存在
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Username already exists: {}", request.username());
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        // 检查邮箱是否存在
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Email already exists: {}", request.email());
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        // 创建新用户
        var user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());

        // 分配默认角色
        roleRepository.findByName("ROLE_USER")
                .ifPresentOrElse(
                        role -> user.getRoles().add(role),
                        () -> {
                            log.error("Default role 'ROLE_USER' not found");
                            throw new BusinessException(ErrorCode.ROLE_NOT_FOUND);
                        }
                );

        var savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", request.username());

        return UserInfo.fromUser(savedUser);
    }
}
