package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사용자 역할을 나타내는 Enum
 */
@Getter
@RequiredArgsConstructor
public enum RoleType {
    SYSTEM_ADMIN("SYSTEM_ADMIN", "시스템 관리자"),
    HR_MANAGER("HR_MANAGER", "인사 관리자"),
    HR_PAYROLL("HR_PAYROLL", "인사 급여 담당"),
    HR_EVALUATION("HR_EVALUATION", "인사 평가 담당"),
    HR_TRANSFER("HR_TRANSFER", "인사 발령 담당"),
    HR_ATTENDANCE("HR_ATTENDANCE", "인사 근태 담당"),
    DEPT_MANAGER("DEPT_MANAGER", "부서 관리자"),
    EMPLOYEE("EMPLOYEE", "일반 사원");

    private final String code;
    private final String description;
}
