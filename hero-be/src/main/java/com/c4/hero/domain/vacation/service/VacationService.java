package com.c4.hero.domain.vacation.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.vacation.dto.DepartmentVacationDTO;
import com.c4.hero.domain.vacation.dto.VacationHistoryDTO;
import com.c4.hero.domain.vacation.dto.VacationSummaryDTO;
import com.c4.hero.domain.vacation.repository.DepartmentVacationRepository;
import com.c4.hero.domain.vacation.repository.VacationRepository;
import com.c4.hero.domain.vacation.repository.VacationSummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: VacationService
 * Description: 휴가 도메인 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/16 (이지윤) 최초 작성 및 코딩 컨벤션 적용
 * 2025/12/30 (리팩토링) Google Calendar 연동 제거
 * </pre>
 *
 * @author 이지윤
 * @version 1.2
 */
@Service
@RequiredArgsConstructor
public class VacationService {

    /** 휴가 이력 조회를 위한 레포지토리 */
    private final VacationRepository vacationRepository;
    private final DepartmentVacationRepository departmentVacationRepository;
    private final VacationSummaryRepository vacationSummaryRepository;
    private final EmployeeRepository employeeRepository;

    /**
     * 직원 휴가 이력을 페이지 단위로 조회합니다.
     */
    public PageResponse<VacationHistoryDTO> findVacationHistory(
            Integer employeeId,
            LocalDate startDate,
            LocalDate endDate,
            int page,
            int size
    ) {
        int safePage = (page <= 0) ? 1 : page;
        int safeSize = (size <= 0) ? 10 : size;

        PageRequest pageable = PageRequest.of(safePage - 1, safeSize);

        Page<VacationHistoryDTO> pageResult = vacationRepository.findVacationHistory(
                employeeId,
                startDate,
                endDate,
                pageable
        );

        return PageResponse.of(
                pageResult.getContent(),
                pageResult.getNumber() + 1,
                pageResult.getSize(),
                (int) pageResult.getTotalElements()
        );
    }

    /**
     * 부서 휴가 캘린더(월 단위) 조회
     */
    public List<DepartmentVacationDTO> findDepartmentVacationCalendar(
            Integer employeeId,
            Integer year,
            Integer month
    ) {
        LocalDate now = LocalDate.now();

        int targetYear = (year != null) ? year : now.getYear();
        int targetMonth = (month != null) ? month : now.getMonthValue();

        if (targetMonth < 1 || targetMonth > 12) {
            throw new IllegalArgumentException("month는 1~12 범위여야 합니다. month=" + targetMonth);
        }

        Integer departmentId = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 employeeId=" + employeeId))
                .getEmployeeDepartment()
                .getDepartmentId();

        LocalDate firstDay = LocalDate.of(targetYear, targetMonth, 1);
        LocalDate monthStart = firstDay;
        LocalDate monthEnd = firstDay.plusMonths(1).minusDays(1);

        return departmentVacationRepository.findApprovedDepartmentVacationByMonth(
                departmentId,
                monthStart,
                monthEnd
        );
    }

    /**
     * 직원의 휴가 요약 정보를 조회합니다.
     */
    public VacationSummaryDTO findVacationLeaves(Integer employeeId) {
        return vacationSummaryRepository.findSummaryByEmployeeId(employeeId);
    }
}
