package com.c4.hero.domain.promotion.controller;

import com.c4.hero.common.response.CustomResponse;
import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.promotion.dto.request.PromotionPlanRequestDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionOptionsDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanDetailResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanResponseDTO;
import com.c4.hero.domain.promotion.service.PromotionCommandService;
import com.c4.hero.domain.promotion.service.PromotionQueryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * <pre>
 * Class Name: PromotionController
 * Description: 승진 관련 API 요청을 처리하는 컨트롤러
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@RestController
@RequestMapping("/api/promotion")
@RequiredArgsConstructor
public class PromotionController {

    private final PromotionCommandService promotionCommandService;
    private final PromotionQueryService promotionQueryService;

    /**
     * 새로운 승진 계획을 등록합니다.
     *
     * @param request 등록할 승진 계획 정보
     * @return 성공 응답
     */
    @PostMapping("/plan")
    public ResponseEntity<CustomResponse<Void>> registerPromotionPlan(@Valid @RequestBody PromotionPlanRequestDTO request) {
        promotionCommandService.registerPromotionPlan(request);
        return ResponseEntity.ok(CustomResponse.success());
    }

    /**
     * 승진 계획 목록을 페이징하여 조회합니다.
     *
     * @param isFinished 조회할 계획의 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @param pageable   페이징 정보 (ex: ?page=0&size=10)
     * @return 페이징된 승진 계획 목록
     */
    @GetMapping("/plan")
    public ResponseEntity<CustomResponse<PageResponse<PromotionPlanResponseDTO>>> getPromotionPlan(
            @RequestParam(required = false) Boolean isFinished,
            Pageable pageable) {
        PageResponse<PromotionPlanResponseDTO> response = promotionQueryService.getPromotionPlan(isFinished, pageable);
        return ResponseEntity.ok(CustomResponse.success(response));
    }

    /**
     * 승진 계획의 상세 정보를 조회합니다.
     *
     * @param promotionId 조회할 승진 계획의 ID
     * @return 승진 계획 상세 정보
     */
    @GetMapping("/plan/{promotionId}")
    public ResponseEntity<CustomResponse<PromotionPlanDetailResponseDTO>> getPromotionPlanDetail(
            @PathVariable int promotionId) {
        PromotionPlanDetailResponseDTO response = promotionQueryService.getPromotionPlanDetail(promotionId);
        return ResponseEntity.ok(CustomResponse.success(response));
    }

    /**
     * 승진 계획 등록에 필요한 옵션(부서, 직급)을 조회합니다.
     *
     * @return 부서 트리 구조와 직급 목록
     */
    @GetMapping("/plan/options")
    public ResponseEntity<CustomResponse<PromotionOptionsDTO>> getPromotionOptions() {
        PromotionOptionsDTO response = promotionQueryService.getPromotionOptions();
        return ResponseEntity.ok(CustomResponse.success(response));
    }
}
