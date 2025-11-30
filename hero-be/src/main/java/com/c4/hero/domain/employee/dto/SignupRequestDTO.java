package com.c4.hero.domain.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * 회원가입 요청을 위한 DTO
 */
@Getter
@Setter
public class SignupRequestDTO {

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    private String employeeName;

    @NotBlank(message = "사번은 필수 입력 항목입니다.")
    private String employeeNumber;

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    private String phone;

    @NotNull(message = "고용 형태는 필수 입력 항목입니다.")
    private String contractType; // 정규직/비정규직/인턴/일일근로자

    @NotBlank(message = "성별은 필수 입력 항목입니다.")
    @Pattern(regexp = "^[MF]$", message = "성별은 'M' 또는 'F' 여야 합니다.")
    private String gender; // M: Male, F: Female

    @NotNull(message = "입사일은 필수 입력 항목입니다.")
    private LocalDate hireDate;

    @NotBlank(message = "이력서 사진 경로는 필수입니다.")
    private String imagePath;

    // --- 선택 입력 항목 ---
    private LocalDate birthDate;
    private String address;
    private String departmentName;
    private String gradeName; // 직급 ID
    private String jobTitleName; // 직책 ID
}
