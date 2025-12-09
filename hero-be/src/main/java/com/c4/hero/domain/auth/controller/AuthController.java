package com.c4.hero.domain.auth.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.domain.auth.service.AuthService;
import com.c4.hero.domain.auth.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * <pre>
 * Class Name: AuthController
 * Description: 인증 관련 API를 처리하는 컨트롤러 (토큰 재발급 등)
 *
 * History
 * 2025/12/09 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Access Token 재발급
     *
     * @param refreshToken Refresh 헤더에 담긴 Refresh Token
     * @return 새로 발급된 Access Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshAccessToken(
            @RequestHeader(JwtUtil.REFRESH_HEADER) String refreshToken) {

        String newAccessToken = authService.refreshAccessToken(refreshToken);

        // 새로 발급된 Access Token을 응답 본문에 담아 반환
        Map<String, String> responseBody = Map.of("accessToken", newAccessToken);
        return ResponseEntity.ok(ApiResponse.success(responseBody));
    }
}
