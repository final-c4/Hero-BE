package com.c4.hero.domain.payroll.policy.controller;

import com.c4.hero.domain.payroll.common.type.ItemType;
import com.c4.hero.domain.payroll.policy.dto.response.ItemPolicyResponse;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyTargetRequest;
import com.c4.hero.domain.payroll.policy.dto.request.ItemPolicyUpsertRequest;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyActivateRequest;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyConfigResponse;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyConfigUpsertRequest;
import com.c4.hero.domain.payroll.policy.dto.request.PolicyCreateRequest;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyDetailResponse;
import com.c4.hero.domain.payroll.policy.dto.response.PolicyResponse;
import com.c4.hero.domain.payroll.policy.service.ItemPolicyService;
import com.c4.hero.domain.payroll.policy.service.PayrollPolicyService;
import com.c4.hero.domain.payroll.policy.service.PayrollPolicyTxService;
import com.c4.hero.domain.payroll.policy.service.PolicyConfigService;
import com.c4.hero.domain.payroll.policy.service.PolicyDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <pre>
 * Controller Name : PayrollPolicyController
 * Description     : 관리자(Admin) 급여 정책(Policy) 관리 API 컨트롤러
 *
 * History
 *  2025/12/24 - 동근 최초 작성
 * </pre>
 *
 * @author 동근
 * @version 1.0
 */
@Tag(
        name = "급여 정책 관리 (Admin)",
        description = "관리자용 급여 정책(Policy), 설정(Config), 항목 정책(Item Policy)을 관리하는 API"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/settings/payroll/policies")
public class PayrollPolicyController {

    private final PayrollPolicyService policyService;
    private final PayrollPolicyTxService policyTxService;
    private final PolicyConfigService configService;
    private final ItemPolicyService itemPolicyService;
    private final PolicyDetailService policyDetailService;

    /**
     *  정책 생성
     *
     * @param req 정책 생성 요청 DTO
     * @return 생성된 정책 정보
     */
    @Operation(
            summary = "급여 정책 생성",
            description = "신규 급여 정책을 생성합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 생성 성공",
                    content = @Content(schema = @Schema(implementation = PolicyResponse.class)))
    })
    @PostMapping
    public ResponseEntity<PolicyResponse> create(@RequestBody PolicyCreateRequest req) {
        return ResponseEntity.ok(policyService.createPolicy(req));
    }

    /**
     * 정책 목록
     *
     * @return 정책 목록
     */
    @Operation(
            summary = "급여 정책 목록 조회",
            description = "등록된 모든 급여 정책 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 목록 조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<PolicyResponse>> list() {
        return ResponseEntity.ok(policyService.getPolicies());
    }

    /**
     * 활성 정책 조회
     *
     * @return 활성 상태의 정책
     */
    @Operation(
            summary = "활성 급여 정책 조회",
            description = "현재 활성화된 급여 정책을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "활성 정책 조회 성공")
    })
    @GetMapping("/active")
    public ResponseEntity<PolicyResponse> active() {
        return ResponseEntity.ok(policyService.getActivePolicy());
    }

    /**
     * 정책 활성화
     *
     * @param policyId 활성화할 정책 ID
     * @param req      활성화 시점/적용 월 등 활성화 파라미터
     * @return 활성화된 정책 정보
     */
    @Operation(
            summary = "급여 정책 활성화",
            description = "지정한 급여 정책을 활성화 처리합니다. (기존 활성 정책은 비활성화)"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정책 활성화 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PatchMapping("/{policyId}/activate")
    public ResponseEntity<PolicyResponse> activate(
            @PathVariable Integer policyId,
            @RequestBody PolicyActivateRequest req
    ) {
        return ResponseEntity.ok(policyTxService.activate(policyId, req));
    }

    /**
     * 정책별 설정(Config) 목록 조회
     *
     * @param policyId 정책 ID
     * @return 설정 목록
     */
    @Operation(
            summary = "정책 설정 목록 조회",
            description = "특정 급여 정책에 포함된 설정(Config) 목록을 조회합니다."
    )
    @GetMapping("/{policyId}/configs")
    public ResponseEntity<List<PolicyConfigResponse>> getConfigs(@PathVariable Integer policyId) {
        return ResponseEntity.ok(configService.getConfigs(policyId));
    }

    /**
     * 정책별 설정(Config) 업서트
     *
     * @param policyId 정책 ID
     * @param reqs     설정 업서트 요청 리스트
     * @return 200 OK
     */
    @Operation(
            summary = "정책 설정 업서트",
            description = "급여 정책의 설정(Config)을 일괄 등록 또는 수정합니다."
    )
    @PutMapping("/{policyId}/configs")
    public ResponseEntity<Void> upsertConfigs(
            @PathVariable Integer policyId,
            @RequestBody List<PolicyConfigUpsertRequest> reqs
    ) {
        configService.upsertConfigs(policyId, reqs);
        return ResponseEntity.ok().build();
    }

    /**
     * 정책별 항목 정책(Item Policy) 목록 조회
     *
     * @param policyId 정책 ID
     * @param type     항목 타입(예: ALLOWANCE/DEDUCTION 등)
     * @return 항목 정책 목록
     */
    @Operation(
            summary = "항목 정책 목록 조회",
            description = "급여 정책에 포함된 항목 정책(Item Policy)을 타입별로 조회합니다."
    )
    @GetMapping("/{policyId}/items")
    public ResponseEntity<List<ItemPolicyResponse>> items(
            @PathVariable Integer policyId,
            @RequestParam ItemType type
    ) {
        return ResponseEntity.ok(itemPolicyService.getItems(policyId, type));
    }

    /**
     * 정책별 항목 정책(Item Policy) 생성
     *
     * @param policyId 정책 ID
     * @param req      항목 정책 생성 요청 DTO
     * @return 생성된 항목 정책
     */
    @Operation(
            summary = "항목 정책 생성",
            description = "급여 정책에 새로운 항목 정책(Item Policy)을 생성합니다."
    )
    @PostMapping("/{policyId}/items")
    public ResponseEntity<ItemPolicyResponse> createItem(
            @PathVariable Integer policyId,
            @RequestBody ItemPolicyUpsertRequest req
    ) {
        return ResponseEntity.ok(itemPolicyService.createItem(policyId, req));
    }

    /**
     * 항목 정책(Item Policy) 수정
     *
     * @param itemPolicyId 항목 정책 ID
     * @param req          항목 정책 수정 요청 DTO
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 수정",
            description = "기존 항목 정책(Item Policy)을 수정합니다."
    )
    @PutMapping("/items/{itemPolicyId}")
    public ResponseEntity<Void> updateItem(
            @PathVariable Integer itemPolicyId,
            @RequestBody ItemPolicyUpsertRequest req
    ) {
        itemPolicyService.updateItem(itemPolicyId, req);
        return ResponseEntity.ok().build();
    }

    /**
     * 항목 정책 대상(Target) 교체
     *
     * @param itemPolicyId 항목 정책 ID
     * @param targets      대상 교체 요청 리스트
     * @return 200 OK
     */
    @Operation(
            summary = "항목 정책 대상 교체",
            description = "항목 정책에 연결된 대상(Target) 목록을 전체 교체합니다."
    )
    @PutMapping("/items/{itemPolicyId}/targets")
    public ResponseEntity<Void> replaceTargets(
            @PathVariable Integer itemPolicyId,
            @RequestBody List<ItemPolicyTargetRequest> targets
    ) {
        itemPolicyService.replaceTargets(itemPolicyId, targets);
        return ResponseEntity.ok().build();
    }

    /**
     * 급여 정책 상세 조회
     *
     * @param policyId 조회할 급여 정책 ID
     * @return 급여 정책 상세 정보
     */
    @Operation(
            summary = "급여 정책 상세 조회",
            description = "급여 정책의 설정(Config) 및 항목 정책(Item Policy)을 포함한 상세 정보를 조회합니다."
    )
    @GetMapping("/{policyId}")
    public ResponseEntity<PolicyDetailResponse> detail(@PathVariable Integer policyId) {
        return ResponseEntity.ok(policyDetailService.getDetail(policyId));
    }
}
