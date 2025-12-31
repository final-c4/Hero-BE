package com.c4.hero.domain.retirement.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.retirement.dto.*;
import com.c4.hero.domain.retirement.entity.ExitReasonMaster;
import com.c4.hero.domain.retirement.entity.Retirement;
import com.c4.hero.domain.retirement.repository.ExitReasonMasterRepository;
import com.c4.hero.domain.retirement.repository.RetirementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <pre>
 * Class Name: RetirementService
 * Description: 퇴직 관리 관련 비즈니스 로직을 처리하는 서비스 클래스
 *
 * History
 * 2025/12/30 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RetirementService {

    private final ExitReasonMasterRepository exitReasonMasterRepository;
    private final EmployeeRepository employeeRepository;
    private final RetirementRepository retirementRepository;

    /**
     * 활성화된 퇴사 사유 목록을 조회합니다.
     *
     * @return 퇴사 사유 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ExitReasonDTO> getExitReasons() {
        List<ExitReasonMaster> reasons = exitReasonMasterRepository.findAll();
        return reasons.stream()
                .filter(reason -> reason.getActiveYn() == 1)
                .map(reason -> ExitReasonDTO.builder()
                        .exitReasonId(reason.getExitReasonId())
                        .reasonName(reason.getReasonName())
                        .reasonType(reason.getReasonType())
                        .detailDescription(reason.getDetailDescription())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 퇴직 현황 요약 정보를 계산하여 반환합니다.
     * 잔존률, 정착률, 종합 이직률, 신입 이직률을 포함합니다.
     *
     * @return 퇴직 현황 요약 DTO
     */
    @Transactional(readOnly = true)
    public RetirementSummaryDTO getRetirementSummary() {
        List<Employee> allEmployees = employeeRepository.findAll();
        long totalEmployees = allEmployees.size();
        
        // 현재 재직 중인 직원 (휴직 포함)
        long retainedEmployees = allEmployees.stream()
                .filter(e -> e.getStatus() != EmployeeStatus.RETIRED)
                .count();
                
        long retiredEmployees = allEmployees.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.RETIRED)
                .count();

        // 잔존률 (전체 직원 중 재직 중인 직원 비율)
        double retentionRate = totalEmployees > 0 ? (double) retainedEmployees / totalEmployees * 100 : 0;

        // 정착률 (3년 이상 재직 중인 비율 / 현재 재직 인원 기준)
        long settledEmployees = allEmployees.stream()
                .filter(e -> e.getStatus() != EmployeeStatus.RETIRED &&
                        ChronoUnit.YEARS.between(e.getHireDate(), LocalDate.now()) >= 3)
                .count();
        double settlementRate = retainedEmployees > 0 ? (double) settledEmployees / retainedEmployees * 100 : 0;

        // 종합 이직률 (전체 퇴사자 / 전체 직원 수)
        double totalTurnoverRate = totalEmployees > 0 ? (double) retiredEmployees / totalEmployees * 100 : 0;

        // 신입 1년 내 이직률
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        List<Employee> newHires = allEmployees.stream()
                .filter(e -> e.getHireDate().isAfter(oneYearAgo))
                .collect(Collectors.toList());
        long newHiresCount = newHires.size();
        long newHiresRetiredCount = newHires.stream()
                .filter(e -> e.getStatus() == EmployeeStatus.RETIRED)
                .count();
        double newHireTurnoverRate = newHiresCount > 0 ? (double) newHiresRetiredCount / newHiresCount * 100 : 0;

        return RetirementSummaryDTO.builder()
                .retentionRate(Math.round(retentionRate * 100.0) / 100.0)
                .settlementRate(Math.round(settlementRate * 100.0) / 100.0)
                .totalTurnoverRate(Math.round(totalTurnoverRate * 100.0) / 100.0)
                .newHireTurnoverRate(Math.round(newHireTurnoverRate * 100.0) / 100.0)
                .build();
    }

    /**
     * 퇴사 사유별 통계 데이터를 집계합니다.
     *
     * @return 사유별 퇴직 통계 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<ExitReasonStatDTO> getExitReasonStats() {
        List<Retirement> retirements = retirementRepository.findAll();
        Map<String, Long> reasonCounts = retirements.stream()
                .collect(Collectors.groupingBy(r -> r.getExitReason().getReasonName(), Collectors.counting()));

        return reasonCounts.entrySet().stream()
                .map(entry -> ExitReasonStatDTO.builder()
                        .reasonName(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * 근속 기간별 잔존율 통계 데이터를 계산합니다.
     * 0년 이상부터 10년 이상까지의 잔존율을 계산합니다.
     *
     * @return 근속 기간별 잔존율 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<TenureRetentionDTO> getTenureRetentionStats() {
        List<Employee> allEmployees = employeeRepository.findAll();
        long totalEmployees = allEmployees.size();
        if (totalEmployees == 0) return new ArrayList<>();

        List<Long> tenures = allEmployees.stream()
                .map(e -> {
                    LocalDate endDate = (e.getStatus() == EmployeeStatus.RETIRED && e.getTerminationDate() != null)
                            ? e.getTerminationDate()
                            : LocalDate.now();
                    return ChronoUnit.YEARS.between(e.getHireDate(), endDate);
                })
                .collect(Collectors.toList());

        List<TenureRetentionDTO> stats = new ArrayList<>();
        for (int i = 0; i <= 10; i++) {
            long year = i;
            long count = tenures.stream().filter(t -> t >= year).count();
            double rate = (double) count / totalEmployees * 100.0;
            stats.add(TenureRetentionDTO.builder()
                    .tenureRange(year + "년 이상")
                    .retentionRate(Math.round(rate * 100.0) / 100.0)
                    .build());
        }

        return stats;
    }

    /**
     * 최근 4분기 동안의 신입 사원 정착률 및 이직률 통계를 계산합니다.
     * 정착률은 수습 기간(3개월)을 기준으로 계산됩니다.
     *
     * @return 신입 정착률 및 이직률 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<NewHireStatDTO> getNewHireStats() {
        List<NewHireStatDTO> stats = new ArrayList<>();
        List<Employee> allEmployees = employeeRepository.findAll();
        LocalDate now = LocalDate.now();

        int currentMonth = now.getMonthValue();
        int currentQ = (currentMonth - 1) / 3 + 1;
        LocalDate currentQStart = LocalDate.of(now.getYear(), (currentQ - 1) * 3 + 1, 1);

        for (int i = 3; i >= 0; i--) {
            LocalDate qStart = currentQStart.minusMonths(i * 3);
            LocalDate qEnd = qStart.plusMonths(3).minusDays(1);

            int qVal = (qStart.getMonthValue() - 1) / 3 + 1;
            String label = qStart.getYear() + "년 " + qVal + "분기";

            List<Employee> hiredInQuarter = allEmployees.stream()
                    .filter(e -> !e.getHireDate().isBefore(qStart) && !e.getHireDate().isAfter(qEnd))
                    .collect(Collectors.toList());

            long total = hiredInQuarter.size();
            
            // 정착률 계산: (재직 중 + 3개월 이상 근무 후 퇴사) / 전체 입사자
            // 재직 중인 직원은 아직 수습 기간 내 이탈하지 않았으므로 정착(과정)으로 간주합니다.
            // 퇴사자의 경우 3개월(90일) 이상 근무했는지를 기준으로 정착 여부를 판단합니다.
            long settledCount = hiredInQuarter.stream()
                    .filter(e -> {
                        if (e.getStatus() != EmployeeStatus.RETIRED) {
                            return true; 
                        }
                        LocalDate endDate = e.getTerminationDate() != null ? e.getTerminationDate() : LocalDate.now();
                        return ChronoUnit.DAYS.between(e.getHireDate(), endDate) >= 90;
                    })
                    .count();

            long retired = hiredInQuarter.stream().filter(e -> e.getStatus() == EmployeeStatus.RETIRED).count();
            
            double settlementRate = total > 0 ? (double) settledCount / total * 100 : 0;
            double turnoverRate = total > 0 ? (double) retired / total * 100 : 0;

            stats.add(NewHireStatDTO.builder()
                    .quarter(label)
                    .settlementRate(Math.round(settlementRate * 100.0) / 100.0)
                    .turnoverRate(Math.round(turnoverRate * 100.0) / 100.0)
                    .build());
        }

        return stats;
    }

    /**
     * 부서별 이직률 통계를 계산합니다.
     *
     * @return 부서별 이직률 DTO 리스트
     */
    @Transactional(readOnly = true)
    public List<DepartmentTurnoverDTO> getDepartmentTurnoverStats() {
        List<Employee> allEmployees = employeeRepository.findAll();

        Map<String, List<Employee>> employeesByDept = allEmployees.stream()
                .filter(e -> e.getEmployeeDepartment() != null)
                .collect(Collectors.groupingBy(e -> e.getEmployeeDepartment().getDepartmentName()));

        return employeesByDept.entrySet().stream()
                .map(entry -> {
                    String deptName = entry.getKey();
                    List<Employee> deptEmployees = entry.getValue();
                    long total = deptEmployees.size();
                    long retired = deptEmployees.stream().filter(e -> e.getStatus() == EmployeeStatus.RETIRED).count();
                    long current = total - retired;
                    double turnoverRate = total > 0 ? (double) retired / total * 100 : 0;

                    return DepartmentTurnoverDTO.builder()
                            .departmentName(deptName)
                            .currentCount(current)
                            .retiredCount(retired)
                            .turnoverRate(Math.round(turnoverRate * 100.0) / 100.0)
                            .build();
                })
                .collect(Collectors.toList());
    }

    /**
     * 퇴사 결재 승인 처리를 수행합니다.
     * 직원 정보에 퇴사일을 업데이트하고, 퇴사 기록을 생성합니다.
     *
     * @param employeeNumber 사번
     * @param terminationDate 퇴사일
     * @param terminationReason 퇴사 사유 (예: "개인 사정")
     * @param terminationReasonDetail 상세 사유
     */
    @Transactional
    public void processRetirementApproval(String employeeNumber, LocalDate terminationDate, String terminationReason, String terminationReasonDetail) {
        // 1. 직원 조회
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 2. 퇴사 사유 조회
        ExitReasonMaster exitReason = exitReasonMasterRepository.findByReasonName(terminationReason)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_INPUT_VALUE)); // 적절한 에러 코드로 변경 필요

        // 3. 직원 정보 업데이트 (퇴사일 설정)
        employee.updateTerminationDate(terminationDate);
        // 주의: 상태(RETIRED)는 스케줄러가 퇴사일이 지난 후 변경함.

        // 4. 근속 일수 계산
        long workingDays = ChronoUnit.DAYS.between(employee.getHireDate(), terminationDate);

        // 5. 퇴사 기록 생성 및 저장
        Retirement retirement = Retirement.builder()
                .employee(employee)
                .exitReason(exitReason)
                .exitDate(terminationDate)
                .detailReason(terminationReasonDetail)
                .workingDays((int) workingDays)
                .build();

        retirementRepository.save(retirement);
    }
}
