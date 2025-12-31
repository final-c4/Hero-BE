package com.c4.hero.domain.retirement.scheduler;

import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * <pre>
 * Class Name: RetirementScheduler
 * Description: 퇴직 관련 자동화 작업을 처리하는 스케줄러 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RetirementScheduler {

    private final EmployeeRepository employeeRepository;

    /**
     * 매일 자정(00:00:00)에 실행되어 퇴사일이 지난 직원의 상태를 퇴직(RETIRED)으로 변경합니다.
     * 퇴사일(terminationDate)이 어제 날짜인 직원을 찾아 상태를 업데이트합니다.
     * 예: 퇴사일이 2024-01-01이면, 2024-01-02 00:00에 상태가 변경됩니다.
     */
    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void updateRetiredStatus() {
        log.info("Starting retirement status update scheduler...");

        // 퇴사일이 어제 이전(포함)이면서 상태가 RETIRED가 아닌 직원 조회
        // 즉, 퇴사일이 지났는데 아직 상태가 변경되지 않은 직원들을 일괄 처리
        List<Employee> employeesToRetire = employeeRepository.findAllByTerminationDateBeforeAndStatusNot(
                LocalDate.now(), EmployeeStatus.RETIRED
        );

        int count = 0;
        for (Employee employee : employeesToRetire) {
            employee.changeStatus(EmployeeStatus.RETIRED);
            count++;
        }
        
        log.info("Completed retirement status update. Processed {} employees.", count);
    }
}
