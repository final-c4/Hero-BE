package com.c4.hero.domain.attendance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * <pre>
 * Entity Name: WorkSystemTemplate
 * Description: 근무제 유형별 기본 출퇴근 시간(템플릿)을 정의하는 엔티티
 *
 * History
 * 2025/12/10 (이지윤) 최초 작성 및 컨벤션 적용
 * </pre>
 *
 * 근무제 유형(예: 기본 근무제, 시차 출퇴근제 등)마다
 * 기본으로 적용되는 출근/퇴근 시간 정보를 관리합니다.
 *
 * @author 이지윤
 * @version 1.0
 */
@Entity
@Table(name = "tbl_work_system_template")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
@Builder
public class WorkSystemTemplate {

    /** 근무제 템플릿 PK (식별자) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "work_system_template_id")
    private Integer id;

    /** 기본 출근 시간 */
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    /** 기본 퇴근 시간 */
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "break_min_minutes", nullable = false)
    private Integer breakMinMinutes;

    @Column(name = "reason", length = 255)
    private String reason;

    /** 이 템플릿이 속한 근무제 유형 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "work_system_type_id")
    private WorkSystemType workSystemType;

    /** 업데이트용 도메인 메서드(Setter 대신) */
    public void update(
            LocalTime startTime,
            LocalTime endTime,
            Integer breakMinMinutes,
            String reason,
            WorkSystemType workSystemType
    ) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakMinMinutes = breakMinMinutes;
        this.reason = reason;
        this.workSystemType = workSystemType;
    }

    public static WorkSystemTemplate create(
            LocalTime startTime,
            LocalTime endTime,
            Integer breakMinMinutes,
            String reason,
            WorkSystemType workSystemType
    ) {
        WorkSystemTemplate t = new WorkSystemTemplate();
        t.startTime = startTime;
        t.endTime = endTime;
        t.breakMinMinutes = breakMinMinutes;
        t.reason = reason;
        t.workSystemType = workSystemType;
        return t;
    }

}
