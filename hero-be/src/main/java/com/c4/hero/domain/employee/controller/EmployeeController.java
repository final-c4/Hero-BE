package com.c4.hero.domain.employee.controller;

import com.c4.hero.common.response.ApiResponse;
import com.c4.hero.domain.employee.dto.SignupRequestDTO;
import com.c4.hero.domain.employee.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * 직원 회원가입
     *
     * @param request 회원가입 요청 정보 DTO
     * @return 성공 시 ApiResponse<Void>
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequestDTO request) {
        employeeService.signup(request);
        return ResponseEntity.ok(ApiResponse.success());
    }
}