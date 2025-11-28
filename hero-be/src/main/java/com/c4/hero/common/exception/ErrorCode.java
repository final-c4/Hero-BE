package com.c4.hero.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * <pre>
 * Enum Name: ErrorCode
 * Description: 애플리케이션 전역 에러 코드 정의
 *
 * - HTTP 상태 코드
 * - 커스텀 에러 코드
 * - 에러 메시지
 *
 * History
 * 2025/11/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // ===== 공통 에러 =====
    /**
     * 잘못된 입력값
     */
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),

    /**
     * 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 오류가 발생했습니다."),

    /**
     * 엔티티를 찾을 수 없음
     */
    ENTITY_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "엔티티를 찾을 수 없습니다."),

    /**
     * 인증 필요
     */
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "C004", "인증이 필요합니다."),

    /**
     * 권한 없음
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "C005", "권한이 없습니다.");

    /** HTTP 상태 코드 */
    private final HttpStatus status;

    /** 커스텀 에러 코드 */
    private final String code;

    /** 에러 메시지 */
    private final String message;
}