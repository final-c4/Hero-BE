package com.c4.hero.domain.payroll.policy.service;

import com.c4.hero.domain.payroll.policy.dto.response.PolicyConfigResponse;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyConfigUpsertRequest;
import com.c4.hero.domain.payroll.policy.entity.PolicyConfig;
import com.c4.hero.domain.payroll.policy.repository.PolicyConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <pre>
 * Class Name : PolicyConfigService
 * Description : 급여 정책 공통 설정(PolicyConfig) 관리 서비스
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
public class PolicyConfigService {

    private final PolicyConfigRepository configRepository;

    /**
     * 특정 급여 정책의 공통 설정 목록 조회
     *
     * @param policyId 급여 정책 ID
     * @return 정책에 속한 공통 설정 목록(응답 DTO)
     */
    public List<PolicyConfigResponse> getConfigs(Integer policyId) {
        return configRepository.findAllByPolicyId(policyId).stream()
                .map(c -> new PolicyConfigResponse(
                        c.getConfigKey(),
                        c.getValueType(),
                        c.getConfigValue(),
                        c.getDescription(),
                        c.getActiveYn()
                ))
                .toList();
    }

    /**
     * 정책 공통 설정 일괄 생성/수정(upsert)
     *  - configKey 기준으로 기존 설정이 있으면 수정, 없으면 신규 생성
     *  - 전달된 설정 목록을 순회하며 각각 독립적으로 처리
     *
     * @param policyId 급여 정책 ID
     * @param reqs 설정 생성/수정 요청 목록
     */
    @Transactional
    public void upsertConfigs(Integer policyId, List<PolicyConfigUpsertRequest> reqs) {
        for (PolicyConfigUpsertRequest req : reqs) {
            if (req.configKey() == null || req.configKey().isBlank())
                throw new IllegalArgumentException("configKey는 필수입니다.");
            if (req.valueType() == null || req.valueType().isBlank())
                throw new IllegalArgumentException("valueType은 필수입니다.");
            if (req.configValue() == null)
                throw new IllegalArgumentException("configValue는 필수입니다.");

            PolicyConfig config = configRepository.findByPolicyIdAndConfigKey(policyId, req.configKey())
                    .orElseGet(() -> PolicyConfig.builder()
                            .policyId(policyId)
                            .configKey(req.configKey())
                            .valueType(req.valueType())
                            .configValue(req.configValue())
                            .description(req.description())
                            .activeYn(req.activeYn() == null ? "Y" : req.activeYn())
                            .build());

            config.apply(req.valueType(), req.configValue(), req.description(), req.activeYn());
            configRepository.save(config);
        }
    }
}
