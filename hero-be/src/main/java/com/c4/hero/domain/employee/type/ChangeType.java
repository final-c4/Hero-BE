package com.c4.hero.domain.employee.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 사원 정보 변경 타입을 나타내는 Enum
 * - C: Create (신규 사원 등록)
 * - U: Update (정보 수정)
 * - TE: Terminate (퇴사/해고)
 * - P: Promotion (승진)
 * - TR: Transfer (부서이동/전근)
 * - O: Other (기타)
 */
@Getter
@RequiredArgsConstructor
public enum ChangeType {
    CREATE("C", "신규 사원 등록"),
    UPDATE("U", "정보 수정"),
    TERMINATE("TE", "퇴사/해고"),
    PROMOTION("P", "승진"),
    TRANSFER("TR", "부서이동/전근"),
    OTHER("O", "기타");

    private final String code;
    private final String description;
}
