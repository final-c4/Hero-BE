package com.c4.hero.domain.vacation.repository;

import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.entity.VacationLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;

public interface VacationRepository extends JpaRepository<VacationLog, Integer> {

    /**
     * 휴가 이력 조회
     *
     * - 특정 직원 기준(employeeId)
     * - 선택적인 기간 필터(startDate / endDate)
     * - VacationHistoryDTO로 바로 Projection
     */
    @Query(
            """
            select new com.c4.hero.domain.vacation.dto.VacationHistoryDTO(
                v.startDate,
                v.endDate,
                vt.vacationTypeName,
                v.reason,
                v.approvalStatus
            )
            from VacationLog v
                join v.vacationType vt
            where v.employee.employeeId = :employeeId
              and (:startDate is null or v.startDate >= :startDate)
              and (:endDate   is null or v.endDate   <= :endDate)
            order by v.startDate desc
            """
    )
    Page<VacationHistoryDTO> findVacationHistory(
            @Param("employeeId") Integer employeeId,
            @Param("startDate") LocalDate startDate,   // null 허용 → 필터 미사용
            @Param("endDate") LocalDate endDate,       // null 허용 → 필터 미사용
            Pageable pageable
    );
}
