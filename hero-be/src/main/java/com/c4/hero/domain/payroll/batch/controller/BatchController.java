package com.c4.hero.domain.payroll.batch.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchDetailResponse;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchListResponse;
import com.c4.hero.domain.payroll.batch.dto.PayrollBatchTargetEmployeeResponse;
import com.c4.hero.domain.payroll.batch.dto.PayrollEmployeeResultResponse;
import com.c4.hero.domain.payroll.batch.mapper.PayrollBatchQueryMapper;
import com.c4.hero.domain.payroll.batch.service.PayrollBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Controller Name : BatchController
 * Description     : 관리자(Admin) 급여 배치 관리 API 컨트롤러
 *
 * History
 *  2025/12/15 - 동근 최초 작성
 *  2025/12/18 - 동근 지급(pay) API 추가
 * </pre>
 *
 *  @author 동근
 *  @version 1.1
 */
@RequestMapping("/api/admin/payroll/batches")
@RequiredArgsConstructor
@RestController
public class BatchController {
    private final PayrollBatchService batchService;
    private final PayrollBatchQueryMapper batchQueryMapper;

    /**
     * 급여 배치 생성
     *
     * @param month 급여월 (YYYY-MM)
     * @return 생성된 급여 배치 ID
     */
    @PostMapping
    public ResponseEntity<Integer> create(@RequestParam String month) {
        return ResponseEntity.ok(batchService.createBatch(month));
    }

    /**
     * 급여 배치 계산 실행
     *
     * @param batchId     급여 배치 ID
     * @param employeeIds 계산 대상 사원 ID 목록 (null일 경우 배치 전체 대상)
     */
    @PostMapping("/{batchId}/calculate")
    public ResponseEntity<Void> calculate(
            @PathVariable Integer batchId,
            @RequestBody(required = false) List<Integer> employeeIds
    ) {
        batchService.calculate(batchId, employeeIds);
        return ResponseEntity.ok().build();
    }

    /**
     * 급여 배치 확정
     *
     * @param batchId 급여 배치 ID
     */
    @PostMapping("/{batchId}/confirm")
    public ResponseEntity<Void> confirm(@PathVariable Integer batchId) {
        batchService.confirm(batchId);
        return ResponseEntity.ok().build();
    }

    /**
     * 급여 배치 목록 조회
     *
     * @param month  급여월 (YYYY-MM), 선택 조건
     * @param status 배치 상태 (READY / CALCULATED / CONFIRMED / PAID), 선택 조건
     * @return 급여 배치 목록 리스트
     */
    @GetMapping
    public List<PayrollBatchListResponse> list(
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String status
    ) {
        return batchQueryMapper.selectBatchList(month, status);
    }

    /**
     * 급여 배치 상세 조회
     *
     * @param batchId 급여 배치 ID
     * @return 급여 배치 상세 정보 및 처리 현황
     */
    @GetMapping("/{batchId}")
    public PayrollBatchDetailResponse detail(@PathVariable Integer batchId) {
        return batchQueryMapper.selectBatchDetail(batchId);
    }

    /**
     * 배치별 사원 급여 계산 결과 조회
     *
     * @param batchId 급여 배치 ID
     * @return 사원별 급여 계산 결과 목록
     */
    @GetMapping("/{batchId}/employees")
    public List<PayrollEmployeeResultResponse> employees(@PathVariable Integer batchId) {
        return batchQueryMapper.selectPayrollEmployees(batchId);
    }

    /**
     * 급여 배치 대상 사원 목록 조회
     *
     * @return 급여 배치 대상 사원 목록
     */
    @GetMapping("/targets")
    public List<PayrollBatchTargetEmployeeResponse> targets() {
        return batchQueryMapper.selectBatchTargetEmployees();
    }

    /**
     * 급여 배치 지급 처리
     *
     * @param batchId 급여 배치 ID
     * @return 공통 성공 응답
     */
    @PostMapping("/{batchId}/pay")
    public ResponseEntity<ApiResponse<Void>> pay(@PathVariable Integer batchId) {
        batchService.pay(batchId);
        return ResponseEntity.ok(ApiResponse.success());
    }
}
