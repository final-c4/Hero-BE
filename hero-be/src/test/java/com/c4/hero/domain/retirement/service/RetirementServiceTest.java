package com.c4.hero.domain.retirement.service;

import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import com.c4.hero.domain.retirement.dto.ExitReasonDTO;
import com.c4.hero.domain.retirement.dto.RetirementSummaryDTO;
import com.c4.hero.domain.retirement.entity.ExitReasonMaster;
import com.c4.hero.domain.retirement.repository.ExitReasonMasterRepository;
import com.c4.hero.domain.retirement.repository.RetirementRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RetirementServiceTest {

    @InjectMocks
    private RetirementService retirementService;

    @Mock
    private ExitReasonMasterRepository exitReasonMasterRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private RetirementRepository retirementRepository;

    @Test
    @DisplayName("퇴사 사유 목록 조회 테스트")
    void getExitReasonsTest() {
        // given
        ExitReasonMaster reason = new ExitReasonMaster();
        // Assuming setters or builder available, or mock
        // reason.setActiveYn(1);
        
        // Mocking behavior instead of setting fields if entity is hard to instantiate
        ExitReasonMaster mockReason = mock(ExitReasonMaster.class);
        given(mockReason.getActiveYn()).willReturn(1);
        given(mockReason.getReasonName()).willReturn("개인 사유");

        given(exitReasonMasterRepository.findAll()).willReturn(Collections.singletonList(mockReason));

        // when
        List<ExitReasonDTO> result = retirementService.getExitReasons();

        // then
        assertEquals(1, result.size());
        assertEquals("개인 사유", result.get(0).getReasonName());
    }

    @Test
    @DisplayName("퇴직 현황 요약 조회 테스트")
    void getRetirementSummaryTest() {
        // given
        Employee emp1 = mock(Employee.class);
        EmployeeDepartment dept = mock(EmployeeDepartment.class);
        given(dept.getDepartmentId()).willReturn(1);
        given(emp1.getEmployeeDepartment()).willReturn(dept);
        given(emp1.getHireDate()).willReturn(LocalDate.now().minusYears(4));
        given(emp1.getStatus()).willReturn(EmployeeStatus.ACTIVE);

        given(employeeRepository.findAll()).willReturn(Collections.singletonList(emp1));

        // when
        RetirementSummaryDTO result = retirementService.getRetirementSummary();

        // then
        assertNotNull(result);
        // Add more assertions based on logic
    }
}
