package com.c4.hero.domain.promotion.mapper;

import com.c4.hero.domain.promotion.dto.response.PromotionPlanDetailResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <pre>
 * Interface Name: PromotionMapper
 * Description: 승진 관련 데이터베이스 접근을 위한 MyBatis 매퍼 인터페이스
 *
 * History
 * 2025/12/19 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Mapper
public interface PromotionMapper {

    /**
     * 조건에 맞는 승진 계획 목록을 페이징하여 조회합니다.
     *
     * @param isFinished 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @param offset     페이지 오프셋
     * @param limit      페이지당 개수
     * @return 승진 계획 목록
     */
    List<PromotionPlanResponseDTO> selectPromotionPlan(@Param("isFinished") Boolean isFinished, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 조건에 맞는 승진 계획의 전체 개수를 조회합니다.
     *
     * @param isFinished 완료 여부 (true: 완료, false: 진행중, null: 전체)
     * @return 전체 개수
     */
    long countPromotionPlan(@Param("isFinished") Boolean isFinished);

    /**
     * 특정 승진 계획의 상세 정보를 조회합니다.
     *
     * @param promotionId 조회할 승진 계획 ID
     * @return 승진 계획 상세 정보 (하위 계획 포함)
     */
    PromotionPlanDetailResponseDTO selectPromotionPlanDetail(int promotionId);
}
