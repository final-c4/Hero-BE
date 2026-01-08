package com.c4.hero.domain.employee.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.dto.request.PasswordChangeRequestDTO;
import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeePasswordServiceTest {

    @InjectMocks
    private EmployeePasswordService passwordService;

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeAccountRepository accountRepository;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private JavaMailSender mailSender;
    @Mock
    private EncryptionUtil encryptionUtil;

    @Test
    @DisplayName("비밀번호 변경 성공 테스트")
    void changePasswordSuccessTest() {
        // given
        Integer employeeId = 1;
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("oldPass");
        request.setNewPassword("newPass");

        given(employeeMapper.findPasswordHashByEmployeeId(employeeId)).willReturn("encodedOldPass");
        given(passwordEncoder.matches("oldPass", "encodedOldPass")).willReturn(true);
        given(passwordEncoder.encode("newPass")).willReturn("encodedNewPass");
        given(employeeMapper.updatePasswordHash(employeeId, "encodedNewPass")).willReturn(1);

        // when
        passwordService.changePassword(employeeId, request);

        // then
        verify(employeeMapper, times(1)).updatePasswordHash(employeeId, "encodedNewPass");
    }

    @Test
    @DisplayName("현재 비밀번호 불일치로 인한 변경 실패 테스트")
    void changePasswordFailTest() {
        // given
        Integer employeeId = 1;
        PasswordChangeRequestDTO request = new PasswordChangeRequestDTO();
        request.setCurrentPassword("wrongPass");

        given(employeeMapper.findPasswordHashByEmployeeId(employeeId)).willReturn("encodedOldPass");
        given(passwordEncoder.matches("wrongPass", "encodedOldPass")).willReturn(false);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> passwordService.changePassword(employeeId, request));
    }

    @Test
    @DisplayName("비밀번호 재설정 토큰 발송 테스트")
    void issueAndSendPasswordResetTokenTest() {
        // given
        String employeeNumber = "2023001";
        String email = "test@test.com";
        Employee employee = mock(Employee.class);

        // @Value 필드 주입
        ReflectionTestUtils.setField(passwordService, "fromEmail", "admin@hero.com");

        given(employeeRepository.findByEmployeeNumber(employeeNumber)).willReturn(Optional.of(employee));
        given(employee.getEmail()).willReturn(new byte[0]);
        given(encryptionUtil.decrypt(any())).willReturn(email);
        given(jwtUtil.createPasswordResetToken(any())).willReturn("token");
        given(mailSender.createMimeMessage()).willReturn(mock(MimeMessage.class));

        // when
        passwordService.issueAndSendPasswordResetToken(employeeNumber, email);

        // then
        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("토큰을 이용한 비밀번호 재설정 테스트")
    void resetPasswordWithTokenTest() {
        // given
        String token = "validToken";
        String newPassword = "newPassword";
        String employeeNumber = "2023001";
        Account account = mock(Account.class);

        given(jwtUtil.validateToken(token)).willReturn(true);
        given(jwtUtil.getUsername(token)).willReturn(employeeNumber);
        given(accountRepository.findByEmployee_EmployeeNumber(employeeNumber)).willReturn(Optional.of(account));
        given(passwordEncoder.encode(newPassword)).willReturn("encodedNewPassword");

        // when
        passwordService.resetPasswordWithToken(token, newPassword);

        // then
        verify(account, times(1)).changePassword("encodedNewPassword");
    }
}
