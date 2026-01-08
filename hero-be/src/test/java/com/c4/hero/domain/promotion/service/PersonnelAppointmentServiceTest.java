package com.c4.hero.domain.promotion.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.repository.EmployeeGradeRepository;
import com.c4.hero.domain.employee.service.EmployeeCommandService;
import com.c4.hero.domain.promotion.dto.request.PromotionReviewRequestDTO;
import com.c4.hero.domain.promotion.entity.PromotionCandidate;
import com.c4.hero.domain.promotion.repository.PromotionCandidateRepository;
import com.c4.hero.domain.promotion.type.PromotionCandidateStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonnelAppointmentServiceTest {

    @InjectMocks
    private PersonnelAppointmentService appointmentService;

    @Mock
    private PromotionCommandService promotionCommandService;
    @Mock
    private EmployeeCommandService employeeCommandService;
    @Mock
    private EmployeeGradeRepository gradeRepository;
    @Mock
    private PromotionCandidateRepository promotionCandidateRepository;

    @Test
    @DisplayName("부서 변경 발령 처리 테스트")
    void processAppointmentDepartmentChangeTest() {
        // given
        Map<String, Object> details = new HashMap<>();
        details.put("changeType", "부서이동");
        details.put("employeeNumber", "2023001");
        details.put("departmentBefore", "인사팀");
        details.put("departmentAfter", "개발팀");

        // when
        appointmentService.processAppointment(details);

        // then
        verify(employeeCommandService, times(1)).updateDepartment("2023001", "개발팀");
    }

    @Test
    @DisplayName("특별 승진 발령 처리 테스트")
    void processAppointmentSpecialPromotionTest() {
        // given
        Map<String, Object> details = new HashMap<>();
        details.put("changeType", "특별승진");
        details.put("employeeNumber", "2023001");
        details.put("gradeBefore", "사원");
        details.put("gradeAfter", "대리");
        details.put("reason", "우수 성과");

        Grade grade = mock(Grade.class);
        given(grade.getGradeId()).willReturn(2);
        given(gradeRepository.findByGrade("대리")).willReturn(Optional.of(grade));

        // when
        appointmentService.processAppointment(details);

        // then
        verify(promotionCommandService, times(1)).confirmDirectPromotion("2023001", 2, "우수 성과");
    }

    @Test
    @DisplayName("정기 승진 발령 처리 테스트")
    void processAppointmentRegularPromotionTest() {
        // given
        Map<String, Object> details = new HashMap<>();
        details.put("changeType", "승진");
        details.put("employeeNumber", "2023001");

        PromotionCandidate candidate = mock(PromotionCandidate.class);
        given(candidate.getCandidateId()).willReturn(100);
        
        given(promotionCandidateRepository.findByEmployee_EmployeeNumberAndStatus("2023001", PromotionCandidateStatus.REVIEW_PASSED))
                .willReturn(Optional.of(candidate));

        // when
        appointmentService.processAppointment(details);

        // then
        verify(promotionCommandService, times(1)).confirmFinalApproval(any(PromotionReviewRequestDTO.class));
    }
}
