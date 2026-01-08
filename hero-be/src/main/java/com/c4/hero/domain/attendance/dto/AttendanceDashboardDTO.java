package com.c4.hero.domain.attendance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * <pre>
 * Class Name : AttendanceDashboardDTO
 * Description : 근태 점수 대시보드 한 행(row)에 대한 DTO
 *
 * History
 * 2025/12/17 이지윤 최초 작성
 * </pre>
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AttendanceDashboardDTO {

    /** 직원 PK */
    private Integer employeeId;

    /** 사번 */
    private String employeeNumber;

    /** 직원 이름 */
    private String employeeName;

    /** 부서 ID */
    private Integer departmentId;

    /** 부서명 */
    private String departmentName;

    /** 지각 횟수 */
    private Long tardyCount;

    /** 결근 횟수 */
    private Long absenceCount;

    /** 근태 점수 (100 - 지각×1 - 결근×2) */
    private Long score;

    /**
     * JPQL 쿼리 결과 매핑을 위한 생성자.
     * JPQL의 sum() 결과 등이 Long이 아닌 Number(Integer 등)로 반환될 경우를 대비하여 Number로 받아서 처리합니다.
     */
    public AttendanceDashboardDTO(
            Integer employeeId,
            String employeeNumber,
            String employeeName,
            Integer departmentId,
            String departmentName,
            Number tardyCount,
            Number absenceCount,
            Number score
    ) {
        this.employeeId = employeeId;
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.tardyCount = tardyCount != null ? tardyCount.longValue() : 0L;
        this.absenceCount = absenceCount != null ? absenceCount.longValue() : 0L;
        this.score = score != null ? score.longValue() : 0L;
    }
}