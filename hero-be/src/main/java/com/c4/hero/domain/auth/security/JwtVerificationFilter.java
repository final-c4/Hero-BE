package com.c4.hero.domain.auth.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class JwtVerificationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // 필터를 적용하지 않을 URL 목록
    private static final List<String> EXCLUDE_URL = List.of("/api/auth/refresh");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String accessToken = jwtUtil.resolveToken(request);

        // 2. 토큰 유효성 검사
        if (StringUtils.hasText(accessToken) && jwtUtil.validateToken(accessToken)) {
            // 3. 토큰이 유효할 경우, 인증 정보 가져오기
            Authentication authentication = jwtUtil.getAuthentication(accessToken);
            // 4. SecurityContext에 인증 정보 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), request.getRequestURI());
        } else {
            log.debug("유효한 JWT 토큰이 없습니다, uri: {}", request.getRequestURI());
        }

        // 다음 필터로 제어 넘김
        filterChain.doFilter(request, response);
    }

    /**
     * 필터를 적용할 필요가 없는 URL인지 확인
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(url -> url.equals(request.getRequestURI()));
    }
}
