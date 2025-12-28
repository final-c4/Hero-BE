package com.c4.hero.domain.employee.service;

import com.c4.hero.common.exception.BusinessException;
import com.c4.hero.common.exception.ErrorCode;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.auth.security.JwtUtil;
import com.c4.hero.domain.employee.entity.Account;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.repository.EmployeeAccountRepository;
import com.c4.hero.domain.employee.repository.EmployeeRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <pre>
 * Class Name: EmployeePasswordServiceImpl
 * Description: 직원 비밀번호 관련 서비스 구현체
 *
 * History
 * 2025/12/29 (승건) 최초 작성
 * </pre>
 *
 * @author 승건
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeePasswordServiceImpl implements EmployeePasswordService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mailSender;
    private final EncryptionUtil encryptionUtil;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Override
    public void changePassword(Integer employeeId, String currentPassword, String newPassword) {
        // 1. 계정 조회
        Account account = accountRepository.findByEmployee_EmployeeId(employeeId)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 2. 현재 비밀번호 검증
        if (!passwordEncoder.matches(currentPassword, account.getPasswordHash())) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "현재 비밀번호가 일치하지 않습니다.");
        }

        // 3. 새 비밀번호 암호화 및 저장
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.changePassword(encodedPassword);
    }

    @Override
    public void issueAndSendPasswordResetToken(String employeeNumber, String email) {
        // 1. 직원 정보 확인
        Employee employee = employeeRepository.findByEmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        // 2. 이메일 일치 여부 확인
        if (!email.equals(encryptionUtil.decrypt(employee.getEmail()))) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "이메일 정보가 일치하지 않습니다.");
        }

        // 3. 비밀번호 재설정 토큰 생성
        String token = jwtUtil.createPasswordResetToken(employee.getEmployeeNumber());

        // 4. 이메일 발송
        sendPasswordResetEmail(email, token);
    }

    @Override
    public void resetPasswordWithToken(String token, String newPassword) {
        // 1. 토큰 검증
        if (!jwtUtil.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE, "유효하지 않은 토큰입니다.");
        }
        // 추가 검증: 토큰 타입이 PASSWORD_RESET인지 확인하면 더 안전함

        // 2. 토큰에서 사번 추출
        String employeeNumber = jwtUtil.getUsername(token);

        // 3. 계정 조회 및 비밀번호 변경
        Account account = accountRepository.findByEmployee_EmployeeNumber(employeeNumber)
                .orElseThrow(() -> new BusinessException(ErrorCode.EMPLOYEE_NOT_FOUND));

        account.changePassword(passwordEncoder.encode(newPassword));
    }

    private void sendPasswordResetEmail(String toEmail, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Hero 시스템] 비밀번호 재설정 안내");

            // TODO: 프론트엔드 URL로 변경 필요
            String resetLink = "http://localhost:5173/reset-password?token=" + token;

            String emailContent = String.format(
                    "안녕하세요, Hero 시스템입니다.<br><br>" +
                            "비밀번호를 재설정하려면 아래 링크를 클릭하세요.<br>" +
                            "링크는 10분간 유효합니다.<br><br>" +
                            "<a href=\"%s\">비밀번호 재설정하기</a><br><br>" +
                            "감사합니다.",
                    resetLink
            );
            helper.setText(emailContent, true);

            mailSender.send(message);
        } catch (MessagingException | MailException e) {
            throw new BusinessException(ErrorCode.EMAIL_SEND_FAILED);
        }
    }
}
