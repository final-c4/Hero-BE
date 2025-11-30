package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 계정 상태를 나타내는 Enum
 * - ACTIVE: 정상 사용
 * - DISABLED: 관리자에 의한 비활성화
 */
@Getter
@RequiredArgsConstructor
public enum AccountStatus {
    ACTIVE("ACTIVE", "정상"),
    DISABLED("DISABLED", "비활성화");

    private final String code;
    private final String description;
}
