package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyResponse;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyTargetResponse;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyWithTargetsResponse;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyConfigResponse;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyDetailResponse;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponse;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicy;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicyTarget;
import com.c4.hero.domain.payroll.policy.entity.PayrollPolicy;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollItemPolicyTargetRepository;
import com.c4.hero.domain.payroll.policy.repository.PayrollPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 * Class Name : PolicyDetailService
 * Description : 급여 정책 상세 조회(Policy Detail) 조립 서비스
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
public class PolicyDetailService {

    private final PayrollPolicyRepository policyRepository;
    private final PolicyConfigService configService;
    private final PayrollItemPolicyRepository itemPolicyRepository;
    private final PayrollItemPolicyTargetRepository targetRepository;

    /**
     * 급여 정책 상세 조회
     *  - 정책 기본 정보 + 공통 설정 + (수당/공제 항목 + 대상)을 조립하여 반환
     *
     * @param policyId 급여 정책 ID
     * @return 정책 상세 응답 DTO
     */
    public PolicyDetailResponse getDetail(Integer policyId) {
        PayrollPolicy p = policyRepository.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책이 존재하지 않습니다. policyId=" + policyId));

        PolicyResponse policy = new PolicyResponse(
                p.getPolicyId(), p.getPolicyName(), p.getStatus(),
                p.getSalaryMonthFrom(), p.getSalaryMonthTo(), p.getActiveYn()
        );

        List<PolicyConfigResponse> configs = configService.getConfigs(policyId);

        Map<ItemType, List<ItemPolicyWithTargetsResponse>> items = new EnumMap<>(ItemType.class);
        items.put(ItemType.ALLOWANCE, loadItemsWithTargets(policyId, ItemType.ALLOWANCE));
        items.put(ItemType.DEDUCTION, loadItemsWithTargets(policyId, ItemType.DEDUCTION));

        return new PolicyDetailResponse(policy, configs, items);
    }

    /**
     * 특정 정책의 항목 정책 목록을 대상(Target) 포함 형태로 로딩
     *  - 항목 정책 엔티티를 조회한 뒤, 각 항목별 대상 목록을 조회하여 함께 묶는다.
     *
     * @param policyId 급여 정책 ID
     * @param type 항목 유형(수당/공제)
     * @return 항목 + 대상 목록 응답 DTO
     */
    private List<ItemPolicyWithTargetsResponse> loadItemsWithTargets(Integer policyId, ItemType type) {
        List<PayrollItemPolicy> itemEntities = itemPolicyRepository.findAllByPolicyIdAndItemType(policyId, type);

        return itemEntities.stream()
                .map(i -> {
                    ItemPolicyResponse item = new ItemPolicyResponse(
                            i.getItemPolicyId(), i.getPolicyId(),
                            i.getItemType(), i.getItemCode(), i.getCalcMethod(),
                            i.getFixedAmount(), i.getRate(),
                            i.getBaseAmountType(), i.getRoundingUnit(), i.getRoundingMode(),
                            i.getSalaryMonthFrom(), i.getSalaryMonthTo(),
                            i.getPriority(), i.getActiveYn()
                    );

                    List<ItemPolicyTargetResponse> targets = targetRepository.findAllByItemPolicyId(i.getItemPolicyId())
                            .stream()
                            .map(this::toTargetResponse)
                            .toList();

                    return new ItemPolicyWithTargetsResponse(item, targets);
                })
                .toList();
    }

    /**
     * 대상 엔티티 → 응답 DTO 변환
     */
    private ItemPolicyTargetResponse toTargetResponse(PayrollItemPolicyTarget t) {
        return new ItemPolicyTargetResponse(t.getPayrollTargetType(), t.getTargetValue());
    }
}
