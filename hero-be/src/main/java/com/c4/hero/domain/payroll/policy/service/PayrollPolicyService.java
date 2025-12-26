package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCreateRequest;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponse;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

/**
 * <pre>
 * Class Name : PayrollPolicyService
 * Description : 급여 정책(PayrollPolicy) 생성/조회 서비스
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PayrollPolicyService {

    private final PayrollPolicyRepository policyRepository;

    /** 급여월 포맷(YYYY-MM) 검증용 정규식 */
    private static final Pattern YM = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    /**
     * 급여 정책 생성
     *  - 요청값 검증 후 정책을 DRAFT 상태로 저장
     *  - 적용 기간은 급여월(YYYY-MM) 기준으로만 관리
     *
     * @param req 정책 생성 요청 DTO
     * @return 생성된 정책 응답 DTO
     */
    @Transactional
    public PolicyResponse createPolicy(PolicyCreateRequest req) {
        if (req.policyName() == null || req.policyName().isBlank())
            throw new IllegalArgumentException("policyName은 필수입니다.");
        validatePeriod(req.salaryMonthFrom(), req.salaryMonthTo());

        PayrollPolicy saved = policyRepository.save(PayrollPolicy.builder()
                .policyName(req.policyName())
                .status(PolicyStatus.DRAFT)
                .salaryMonthFrom(req.salaryMonthFrom())
                .salaryMonthTo(req.salaryMonthTo())
                .activeYn("Y")
                .build());

        return new PolicyResponse(
                saved.getPolicyId(),
                saved.getPolicyName(),
                saved.getStatus(),
                saved.getSalaryMonthFrom(),
                saved.getSalaryMonthTo(),
                saved.getActiveYn()
        );
    }

    /**
     * 급여 정책 목록 조회
     *
     * @return 정책 목록(응답 DTO)
     */
    public List<PolicyResponse> getPolicies() {
        return policyRepository.findAll().stream()
                .map(p -> new PolicyResponse(
                        p.getPolicyId(),
                        p.getPolicyName(),
                        p.getStatus(),
                        p.getSalaryMonthFrom(),
                        p.getSalaryMonthTo(),
                        p.getActiveYn()
                ))
                .toList();
    }
    /**
     * 현재 ACTIVE 상태의 정책 조회
     *  - 활성 정책이 없으면 예외를 발생
     *
     * @return 활성 정책(응답 DTO)
     */
    public PolicyResponse getActivePolicy() {
        PayrollPolicy p = policyRepository.findTop1ByStatusOrderByPolicyIdDesc(PolicyStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("ACTIVE 정책이 없습니다."));

        return new PolicyResponse(
                p.getPolicyId(),
                p.getPolicyName(),
                p.getStatus(),
                p.getSalaryMonthFrom(),
                p.getSalaryMonthTo(),
                p.getActiveYn()
        );
    }

    /**
     *
     * @param from : 필수, YYYY-MM 형식
     * @param to : 선택, YYYY-MM 형식이며 from <= to
     */
    private void validatePeriod(String from, String to) {
        if (from == null || from.isBlank()) {
            throw new IllegalArgumentException("salaryMonthFrom은 필수입니다.");
        }
        if (!YM.matcher(from).matches()) {
            throw new IllegalArgumentException("salaryMonthFrom 형식이 올바르지 않습니다. (YYYY-MM)");
        }

        if (to != null && !to.isBlank()) {
            if (!YM.matcher(to).matches()) {
                throw new IllegalArgumentException("salaryMonthTo 형식이 올바르지 않습니다. (YYYY-MM)");
            }
            if (from.compareTo(to) > 0) {
                throw new IllegalArgumentException("salaryMonthFrom은 salaryMonthTo보다 클 수 없습니다.");
            }
        }
    }
}
