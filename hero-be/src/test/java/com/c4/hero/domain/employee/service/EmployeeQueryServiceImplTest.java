package com.c4.hero.domain.employee.service;

import com.c4.hero.common.response.PageResponse;
import com.c4.hero.common.s3.S3Service;
import com.c4.hero.common.util.EncryptionUtil;
import com.c4.hero.domain.employee.dto.request.EmployeeSearchDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeDetailResponseDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeListResponseDTO;
import com.c4.hero.domain.employee.entity.Employee;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import com.c4.hero.domain.employee.entity.Grade;
import com.c4.hero.domain.employee.entity.JobTitle;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import com.c4.hero.domain.employee.type.EmployeeStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class EmployeeQueryServiceImplTest {

    @InjectMocks
    private EmployeeQueryServiceImpl queryService;

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private EncryptionUtil encryptionUtil;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("직원 목록 조회 테스트")
    void getEmployeesTest() {
        // given
        EmployeeSearchDTO searchDTO = new EmployeeSearchDTO();
        searchDTO.setPage(1);
        searchDTO.setSize(10);

        Employee employee = mock(Employee.class);
        given(employee.getEmployeeId()).willReturn(1);
        given(employee.getEmployeeName()).willReturn("홍길동");
        
        given(employeeMapper.findWithPaging(searchDTO)).willReturn(Collections.singletonList(employee));
        given(employeeMapper.count(searchDTO)).willReturn(1);

        // when
        PageResponse<EmployeeListResponseDTO> result = queryService.getEmployees(searchDTO);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals("홍길동", result.getContent().get(0).getEmployeeName());
    }

    @Test
    @DisplayName("직원 상세 조회 테스트")
    void findByIdTest() {
        // given
        Integer employeeId = 1;
        Employee employee = mock(Employee.class);
        given(employee.getEmployeeId()).willReturn(employeeId);
        given(employee.getStatus()).willReturn(EmployeeStatus.ACTIVE);
        given(employee.getHireDate()).willReturn(LocalDate.now().minusYears(1));
        
        // Mocking encrypted fields to be non-null
        given(employee.getEmail()).willReturn(new byte[]{1, 2, 3});
        given(employee.getPhone()).willReturn(new byte[]{4, 5, 6});
        given(employee.getAddress()).willReturn(new byte[]{7, 8, 9});
        
        given(employeeMapper.findById(employeeId)).willReturn(Optional.of(employee));
        given(encryptionUtil.decrypt(any())).willReturn("decryptedData");
        given(s3Service.generatePresignedUrl(any())).willReturn("http://image.url");

        // when
        EmployeeDetailResponseDTO result = queryService.findById(employeeId);

        // then
        assertNotNull(result);
        assertEquals(employeeId, result.getEmployeeId());
        assertEquals("decryptedData", result.getEmail());
    }
}
