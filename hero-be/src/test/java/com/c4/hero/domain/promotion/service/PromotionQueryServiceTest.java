package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.promotion.dto.response.PromotionOptionsResponseDTO;
import com.c4.hero.domain.promotion.dto.response.PromotionPlanResponseDTO;
import com.c4.hero.domain.promotion.mapper.PromotionMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PromotionQueryServiceTest {

    @InjectMocks
    private PromotionQueryService promotionService;

    @Mock
    private PromotionMapper promotionMapper;
    @Mock
    private EmployeeDepartmentRepository departmentRepository;
    @Mock
    private EmployeeGradeRepository gradeRepository;

    @Test
    @DisplayName("승진 계획 목록 조회 테스트")
    void getPromotionPlanTest() {
        // given
        Boolean isFinished = false;
        Pageable pageable = PageRequest.of(0, 10);
        
        given(promotionMapper.countPromotionPlan(isFinished)).willReturn(1L);
        given(promotionMapper.selectPromotionPlan(isFinished, 0, 10))
                .willReturn(Collections.singletonList(new PromotionPlanResponseDTO()));

        // when
        PageResponse<PromotionPlanResponseDTO> result = promotionService.getPromotionPlan(isFinished, pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }

    @Test
    @DisplayName("승진 계획 옵션 조회 테스트")
    void getPromotionOptionsTest() {
        // given
        EmployeeDepartment dept = new EmployeeDepartment(); // Builder or Setter needed if not available
        // Assuming default constructor and field access or mock
        
        Grade grade = new Grade();
        
        given(departmentRepository.findAll(any(Sort.class))).willReturn(Collections.emptyList());
        given(gradeRepository.findAll(any(Sort.class))).willReturn(Collections.emptyList());

        // when
        PromotionOptionsResponseDTO result = promotionService.getPromotionOptions();

        // then
        assertNotNull(result);
        assertNotNull(result.getPromotionDepartmentDTOList());
        assertNotNull(result.getPromotionGradeDTOList());
    }

    @Test
    @DisplayName("추천 가능한 승진 계획 조회 테스트")
    void getRecommendPromotionPlanTest() {
        // given
        Integer departmentId = 1;
        EmployeeDepartment dept = new EmployeeDepartment();
        // Mocking department hierarchy logic if needed, or just simple return
        // findAllSubDepartmentIds relies on departmentRepository.findByParentDepartmentId
        
        given(departmentRepository.findByParentDepartmentId(departmentId)).willReturn(Collections.emptyList());
        given(promotionMapper.selectRecommendPromotionPlan(anyList())).willReturn(Collections.singletonList(new PromotionPlanResponseDTO()));

        // when
        List<PromotionPlanResponseDTO> result = promotionService.getRecommendPromotionPlan(departmentId);

        // then
        assertEquals(1, result.size());
    }
}
