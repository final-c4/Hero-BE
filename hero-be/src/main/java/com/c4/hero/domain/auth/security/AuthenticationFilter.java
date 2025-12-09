package com.c4.hero.domain.auth.security;

import com.c4.hero.domain.auth.dto.RequestLoginDTO;
import tools.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 로그인 요청 시 인증을 시도하는 메소드
     */
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // 1. 요청 본문(JSON)을 DTO로 변환
            ObjectMapper objectMapper = new ObjectMapper();
            RequestLoginDTO loginDTO = objectMapper.readValue(request.getInputStream(), RequestLoginDTO.class);

            log.info("로그인 시도: {}", loginDTO.getAccount());

            // 2. 인증용 토큰(UsernamePasswordAuthenticationToken) 생성
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(loginDTO.getAccount(), loginDTO.getPassword(), null);

            // 3. AuthenticationManager에 토큰을 전달하여 인증 시도
            // -> 내부적으로 UserDetailsService의 loadUserByUsername 실행
            return authenticationManager.authenticate(authenticationToken);

        } catch (IOException e) {
            log.error("로그인 요청 처리 중 오류 발생", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 인증 성공 시 호출되는 메소드
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("로그인 성공: {}", authResult.getName());

        // 1. Access Token, Refresh Token 생성
        String accessToken = jwtUtil.createAccessToken(authResult);
        String refreshToken = jwtUtil.createRefreshToken(authResult);

        // 2. 응답 헤더에 토큰 추가
        response.addHeader(JwtUtil.AUTHORIZATION_HEADER, JwtUtil.BEARER_PREFIX + accessToken);
        response.addHeader(JwtUtil.REFRESH_HEADER, refreshToken); // Bearer 접두사 없이 추가

        // (선택) 응답 본문에 사용자 정보 추가
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"message\": \"로그인 성공\"}");
    }

    /**
     * 인증 실패 시 호출되는 메소드
     */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn("로그인 실패: {}", failed.getMessage());

        // 401 Unauthorized 에러 응답
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("{\"error\": \"로그인 실패\", \"message\": \"" + failed.getMessage() + "\"}");
    }
}
