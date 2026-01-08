package com.c4.hero.domain.employee.service;

import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.employee.dto.request.ContactUpdateRequestDTO;
import com.c4.hero.domain.employee.dto.response.EmployeeProfileResponseDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmployeeProfileQueryServiceTest {

    @InjectMocks
    private EmployeeProfileQueryService profileService;

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("직원 ID로 프로필 조회 테스트")
    void getProfileByEmployeeIdTest() {
        // given
        Integer employeeId = 1;
        String secretKey = "secret";
        ReflectionTestUtils.setField(profileService, "secretKey", secretKey);

        EmployeeProfileResponseDTO mockProfile = new EmployeeProfileResponseDTO();
        mockProfile.setEmployeeName("홍길동");
        mockProfile.setSealImageUrl("seal.png");

        given(employeeMapper.findProfileByEmployeeId(employeeId, secretKey)).willReturn(mockProfile);
        given(s3Service.generatePresignedUrl("seal.png")).willReturn("http://presigned.url");

        // when
        EmployeeProfileResponseDTO result = profileService.getProfileByEmployeeId(employeeId);

        // then
        assertNotNull(result);
        assertEquals("홍길동", result.getEmployeeName());
        assertEquals("http://presigned.url", result.getSealImageUrl());
    }

    @Test
    @DisplayName("연락처 정보 수정 테스트")
    void updateContactInfoTest() {
        // given
        Integer employeeId = 1;
        String secretKey = "secret";
        ReflectionTestUtils.setField(profileService, "secretKey", secretKey);
        
        ContactUpdateRequestDTO request = new ContactUpdateRequestDTO();
        request.setEmail("new@test.com");

        given(employeeMapper.findProfileByEmployeeId(employeeId, secretKey)).willReturn(new EmployeeProfileResponseDTO());
        given(employeeMapper.updateContactInfo(eq(employeeId), any(), eq(secretKey))).willReturn(1);

        // when
        profileService.updateContactInfo(employeeId, request);

        // then
        verify(employeeMapper, times(1)).updateContactInfo(eq(employeeId), any(), eq(secretKey));
    }
}
