package com.c4.hero.domain.auth.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.auth.security.CustomUserDetails;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name: AuthServiceImpl
 * Description: AuthService의 구현체
 *
 * History
 * 2025/12/09 (이승건) 최초 작성
 * </pre>
 *
 * @author 이승건
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final JwtUtil jwtUtil;
    /**
     * 사용자 이름(계정 ID)으로 사용자 정보를 조회하여 UserDetails 객체로 반환
     *
     * @param username the username identifying the user whose data is required.
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no
     *                                   GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByAccountWithRoles(username)
                .map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        // 1. Refresh Token 유효성 검증
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. Refresh Token에서 사용자 정보(username) 추출
        String username = jwtUtil.getUsername(refreshToken);

        // 3. DB에서 최신 사용자 정보 조회
        UserDetails userDetails = loadUserByUsername(username);

        // 4. 새로운 Access Token 생성을 위한 Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        // 5. 새로운 Access Token 생성 및 반환
        return jwtUtil.createAccessToken(authentication);
    }
}
