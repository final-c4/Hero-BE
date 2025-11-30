package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 직원 상태를 나타내는 Enum
 * - A: 재직
 * - O: 휴직
 * - R: 퇴직
 */
@Getter
@RequiredArgsConstructor
public enum EmployeeStatus {
    ACTIVE("A", "재직"),
    ON_LEAVE("O", "휴직"),
    RETIRED("R", "퇴직");

    private final String code;
    private final String description;
}
