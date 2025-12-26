package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.payroll.common.type.PolicyStatus;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyActivateRequest;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponse;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Pattern;
/**
 * <pre>
 * Class Name : PayrollPolicyTxService
 * Description : 급여 정책(PayrollPolicy) 상태 전이 트랜잭션 서비스
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
public class PayrollPolicyTxService {

    private final PayrollPolicyRepository policyRepository;

    /** 급여월 포맷(YYYY-MM) 검증용 정규식 */
    private static final Pattern YM = Pattern.compile("^\\d{4}-(0[1-9]|1[0-2])$");

    /**
     * 급여 정책 활성화 처리
     *  - 기존 ACTIVE 정책이 존재하면 필요 시 만료(EXPIRED) 처리
     *  - 지정된 정책을 ACTIVE로 전환하고 적용 기간을 확정
     *
     * @param policyId 활성화할 정책 ID
     * @param req 활성화 요청(적용 시작/종료 급여월)
     * @return 활성화된 정책 응답 DTO
     */
    @Transactional
    public PolicyResponse activate(Integer policyId, PolicyActivateRequest req) {
        validatePeriod(req.salaryMonthFrom(), req.salaryMonthTo());

        PayrollPolicy target = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다. policyId=" + policyId));

        policyRepository.findTop1ByStatusOrderByPolicyIdDesc(PolicyStatus.ACTIVE)
                .ifPresent(active -> {
                    if (!active.getPolicyId().equals(target.getPolicyId())) active.expire();
                });

        target.activate(req.salaryMonthFrom(), req.salaryMonthTo());

        PayrollPolicy saved = policyRepository.save(target);

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
     * 정책 적용 기간(급여월) 검증
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
