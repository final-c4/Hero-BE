package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.SignupRequestDTO;

public interface EmployeeService {

    /**
     * 직원 회원가입 처리
     *
     * @param request 회원가입 요청 DTO
     */
    void signup(SignupRequestDTO request);
}
