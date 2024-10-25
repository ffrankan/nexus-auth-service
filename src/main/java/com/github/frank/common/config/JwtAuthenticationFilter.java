package com.github.frank.common.config;

import com.github.frank.auth.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * @author Frank An
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. 获取token
            String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

            // 2. 如果没有token或格式不对，直接放行
            if (!hasValidAuthorizationHeader(authHeader)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 3. 提取和验证token
            String jwt = authHeader.substring(BEARER_PREFIX.length());

            // 4. 如果token已经过期或无效，直接放行
            if (!jwtService.validateToken(jwt)) {
                filterChain.doFilter(request, response);
                return;
            }

            // 5. 获取用户详情
            var tokenInfo = jwtService.parseToken(jwt);
            var userDetails = userDetailsService.loadUserByUsername(tokenInfo.username());

            // 6. 创建认证令牌并设置到上下文
            UsernamePasswordAuthenticationToken authenticated = UsernamePasswordAuthenticationToken
                    .authenticated(userDetails, jwt, userDetails.getAuthorities());
            // setDetails() 是用来存储额外的请求相关信息的。
            // WebAuthenticationDetails 默认会记录两个信息：
            // 1.远程地址（客户端IP）
            // 2. 会话ID（如果有的话）
            authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticated);

            // 7. 放行请求
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
        }
    }

    private boolean hasValidAuthorizationHeader(String authHeader) {
        return Objects.nonNull(authHeader) &&
                authHeader.startsWith(BEARER_PREFIX) &&
                authHeader.length() > BEARER_PREFIX.length();
    }

}
