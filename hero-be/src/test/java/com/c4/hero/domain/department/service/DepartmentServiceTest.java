package com.c4.hero.domain.department.service;

import com.c4.hero.common.s3.S3Service;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.dto.EmployeeDepartmentHistoryDTO;
import com.c4.hero.domain.department.dto.EmployeeGradeHistoryDTO;
import com.c4.hero.domain.department.dto.OrganizationNodeDTO;
import com.c4.hero.domain.department.repository.DepartmentRepository;
import com.c4.hero.domain.employee.entity.*;
import com.c4.hero.domain.employee.repository.EmployeeDepartmentHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeGradeHistoryRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    @InjectMocks
    private DepartmentService departmentService;

    @Mock
    private DepartmentRepository departmentRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeDepartmentHistoryRepository employeeDepartmentHistoryRepository;
    @Mock
    private EmployeeGradeHistoryRepository employeeGradeHistoryRepository;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("전체 부서 목록 조회 테스트")
    void getDepartmentsTest() {
        // given
        EmployeeDepartment dept1 = EmployeeDepartment.builder().departmentId(1).departmentName("인사팀").build();
        EmployeeDepartment dept2 = EmployeeDepartment.builder().departmentId(2).departmentName("개발팀").build();
        given(departmentRepository.findAll()).willReturn(Arrays.asList(dept1, dept2));

        // when
        List<DepartmentDTO> result = departmentService.getDepartments();

        // then
        assertEquals(2, result.size());
        assertEquals("인사팀", result.get(0).getDepartmentName());
        assertEquals("개발팀", result.get(1).getDepartmentName());
    }

    @Test
    @DisplayName("조직도 트리 구조 조회 테스트")
    void getOrganizationChartTest() {
        // given
        EmployeeDepartment parentDept = EmployeeDepartment.builder()
                .departmentId(1)
                .departmentName("경영지원본부")
                .parentDepartmentId(null)
                .build();
        EmployeeDepartment childDept = EmployeeDepartment.builder()
                .departmentId(2)
                .departmentName("인사팀")
                .parentDepartmentId(1)
                .build();

        Employee employee = mock(Employee.class);
        given(employee.getEmployeeDepartment()).willReturn(childDept);
        given(employee.getStatus()).willReturn(EmployeeStatus.ACTIVE);
        given(employee.getEmployeeId()).willReturn(100);
        given(employee.getEmployeeName()).willReturn("홍길동");

        given(departmentRepository.findAll()).willReturn(Arrays.asList(parentDept, childDept));
        given(employeeRepository.findAllByStatusNot(EmployeeStatus.RETIRED)).willReturn(Collections.singletonList(employee));
        given(s3Service.generatePresignedUrl(any())).willReturn("http://image.url");

        // when
        List<OrganizationNodeDTO> result = departmentService.getOrganizationChart();

        // then
        assertNotNull(result);
        assertEquals(1, result.size()); // Root node count
        assertEquals("경영지원본부", result.get(0).getDepartmentName());
        assertEquals(1, result.get(0).getChildren().size());
        assertEquals("인사팀", result.get(0).getChildren().get(0).getDepartmentName());
        assertEquals(1, result.get(0).getChildren().get(0).getEmployees().size());
        assertEquals("홍길동", result.get(0).getChildren().get(0).getEmployees().get(0).getEmployeeName());
    }

    @Test
    @DisplayName("부서 변경 이력 조회 테스트")
    void getDepartmentHistoryTest() {
        // given
        Integer employeeId = 1;
        Employee employee = mock(Employee.class);
        given(employee.getEmployeeId()).willReturn(employeeId);

        EmployeeDepartmentHistory history = EmployeeDepartmentHistory.builder()
                .employeeHistoryId(1)
                .employee(employee)
                .changeType(ChangeType.TRANSFER)
                .departmentName("인사팀")
                .changedAt(LocalDateTime.now())
                .build();

        given(employeeDepartmentHistoryRepository.findByEmployee_EmployeeIdOrderByChangedAtDesc(employeeId))
                .willReturn(Collections.singletonList(history));

        // when
        List<EmployeeDepartmentHistoryDTO> result = departmentService.getDepartmentHistory(employeeId);

        // then
        assertEquals(1, result.size());
        assertEquals("인사팀", result.get(0).getDepartmentName());
        assertEquals(ChangeType.TRANSFER.getDescription(), result.get(0).getChangeType());
    }

    @Test
    @DisplayName("직급 변경 이력 조회 테스트")
    void getGradeHistoryTest() {
        // given
        Integer employeeId = 1;
        Employee employee = mock(Employee.class);
        given(employee.getEmployeeId()).willReturn(employeeId);

        EmployeeGradeHistory history = EmployeeGradeHistory.builder()
                .employeeHistoryId(1)
                .employee(employee)
                .changeType(ChangeType.PROMOTION)
                .gradeName("대리")
                .changedAt(LocalDateTime.now())
                .build();

        given(employeeGradeHistoryRepository.findByEmployee_EmployeeIdOrderByChangedAtDesc(employeeId))
                .willReturn(Collections.singletonList(history));

        // when
        List<EmployeeGradeHistoryDTO> result = departmentService.getGradeHistory(employeeId);

        // then
        assertEquals(1, result.size());
        assertEquals("대리", result.get(0).getGradeName());
        assertEquals(ChangeType.PROMOTION.getDescription(), result.get(0).getChangeType());
    }
}
