package com.c4.hero.domain.employee.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.dto.request.SignupRequestDTO;
import com.c4.hero.domain.employee.entity.*;
import com.c4.hero.domain.employee.repository.*;
import com.c4.hero.domain.employee.type.RoleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import jakarta.mail.internet.MimeMessage;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeCommandServiceImplTest {

    @InjectMocks
    private EmployeeCommandServiceImpl employeeService;

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeDepartmentRepository departmentRepository;
    @Mock
    private EmployeeGradeRepository gradeRepository;
    @Mock
    private EmployeeJobTitleRepository jobTitleRepository;
    @Mock
    private EmployeeAccountRepository accountRepository;
    @Mock
    private EmployeeRoleRepository roleRepository;
    @Mock
    private EmployeeAccountRoleRepository accountRoleRepository;
    @Mock
    private EmployeeDepartmentHistoryRepository employeeDepartmentHistoryRepository;
    @Mock
    private EmployeeGradeHistoryRepository employeeGradeHistoryRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private S3Service s3Service;
    @Mock
    private EncryptionUtil encryptionUtil;

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccessTest() {
        // given
        SignupRequestDTO request = new SignupRequestDTO();
        request.setEmployeeNumber("2023001");
        request.setEmployeeName("홍길동");
        request.setEmail("test@test.com");
        request.setPhone("010-1234-5678");
        request.setDepartmentName("개발팀");
        request.setGradeName("사원");
        request.setJobTitleName("팀원");

        // @Value 필드 주입
        ReflectionTestUtils.setField(employeeService, "defaultMailSenderUsername", "admin@hero.com");

        given(encryptionUtil.encrypt(anyString())).willReturn(new byte[0]);
        given(employeeRepository.findByEmployeeNumberOrEmailOrPhone(any(), any(), any())).willReturn(Optional.empty());
        given(departmentRepository.findByDepartmentName(any())).willReturn(Optional.of(mock(EmployeeDepartment.class)));
        given(gradeRepository.findByGrade(any())).willReturn(Optional.of(mock(Grade.class)));
        given(jobTitleRepository.findByJobTitle(any())).willReturn(Optional.of(mock(JobTitle.class)));
        given(employeeRepository.save(any(Employee.class))).willReturn(mock(Employee.class));
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(roleRepository.findByRole(RoleType.EMPLOYEE)).willReturn(Optional.of(mock(Role.class)));
        given(mailSender.createMimeMessage()).willReturn(mock(MimeMessage.class));

        // when
        employeeService.signup(request);

        // then
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("중복 사번으로 인한 회원가입 실패 테스트")
    void signupDuplicateEmployeeNumberTest() {
        // given
        SignupRequestDTO request = new SignupRequestDTO();
        request.setEmployeeNumber("2023001");
        request.setEmail("test@test.com");
        request.setPhone("010-1234-5678");

        Employee existingEmployee = mock(Employee.class);
        given(existingEmployee.getEmployeeNumber()).willReturn("2023001");

        given(encryptionUtil.encrypt(anyString())).willReturn(new byte[0]);
        given(employeeRepository.findByEmployeeNumberOrEmailOrPhone(any(), any(), any()))
                .willReturn(Optional.of(existingEmployee));

        // when & then
        assertThrows(BusinessException.class, () -> employeeService.signup(request));
    }

    @Test
    @DisplayName("부서 변경 테스트")
    void updateDepartmentTest() {
        // given
        String employeeNumber = "2023001";
        String newDeptName = "인사팀";
        Employee employee = mock(Employee.class);
        EmployeeDepartment newDept = mock(EmployeeDepartment.class);

        given(employeeRepository.findByEmployeeNumber(employeeNumber)).willReturn(Optional.of(employee));
        given(departmentRepository.findByDepartmentName(newDeptName)).willReturn(Optional.of(newDept));
        // 불필요한 스터빙 제거: given(newDept.getDepartmentName()).willReturn(newDeptName);

        // when
        employeeService.updateDepartment(employeeNumber, newDeptName);

        // then
        verify(employee, times(1)).changeDepartment(newDept);
        verify(employeeDepartmentHistoryRepository, times(1)).save(any());
    }

    @Test
    @DisplayName("직책 변경 테스트")
    void updateJobTitleTest() {
        // given
        String employeeNumber = "2023001";
        String newJobTitleName = "팀장";
        Employee employee = mock(Employee.class);
        JobTitle newJobTitle = mock(JobTitle.class);

        given(employeeRepository.findByEmployeeNumber(employeeNumber)).willReturn(Optional.of(employee));
        given(jobTitleRepository.findByJobTitle(newJobTitleName)).willReturn(Optional.of(newJobTitle));

        // when
        employeeService.updateJobTitle(employeeNumber, newJobTitleName);

        // then
        verify(employee, times(1)).changeJobTitle(newJobTitle);
    }
}
