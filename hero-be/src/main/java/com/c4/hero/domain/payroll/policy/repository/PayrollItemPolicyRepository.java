package com.c4.hero.domain.payroll.policy.repository;

import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.policy.entity.PayrollItemPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * <pre>
 * Class Name : PayrollItemPolicyRepository
 * Description : 급여 항목 정책(PayrollItemPolicy) 조회를 위한 Repository
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
public interface PayrollItemPolicyRepository extends JpaRepository<PayrollItemPolicy, Integer> {
    /**
     * 정책 ID와 항목 타입 기준으로 항목 정책 목록 조회
     *
     * @param policyId 급여 정책 ID
     * @param itemType 항목 유형 (수당 / 공제 등)
     * @return 항목 정책 목록
     */
    List<PayrollItemPolicy> findAllByPolicyIdAndItemType(Integer policyId, ItemType itemType);

    /**
     * 정책 ID, 항목 타입, 활성 여부 기준으로 항목 정책 목록 조회
     *  - 화면 기본 조회 시 활성 항목만 노출하는 용도로 사용
     *
     * @param policyId 급여 정책 ID
     * @param itemType 항목 유형
     * @param activeYn 활성 여부 (Y/N)
     * @return 활성 상태 기준 항목 정책 목록
     */
    List<PayrollItemPolicy> findAllByPolicyIdAndItemTypeAndActiveYn(Integer policyId, ItemType itemType, String activeYn);
}
