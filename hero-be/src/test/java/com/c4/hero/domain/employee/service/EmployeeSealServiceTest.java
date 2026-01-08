package com.c4.hero.domain.employee.service;

import com.c4.hero.common.s3.S3Service;
import com.c4.hero.domain.employee.dto.request.SealTextUpdateRequestDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeSealServiceTest {

    @InjectMocks
    private EmployeeSealService sealService;

    @Mock
    private EmployeeMapper employeeMapper;
    @Mock
    private S3Service s3Service;

    @Test
    @DisplayName("텍스트 직인 업데이트 테스트")
    void updateSealTextTest() {
        // given
        Integer employeeId = 1;
        SealTextUpdateRequestDTO request = new SealTextUpdateRequestDTO();
        request.setSealText("홍길동인");

        given(employeeMapper.findSealImageUrlByEmployeeId(employeeId)).willReturn("old_seal.png");
        given(s3Service.uploadFile(any(MultipartFile.class), anyString())).willReturn("new_seal_key");
        given(employeeMapper.updateSealImageUrl(employeeId, "new_seal_key")).willReturn(1);

        // when
        sealService.updateSealText(employeeId, request);

        // then
        verify(s3Service, times(1)).deleteFile("old_seal.png");
        verify(s3Service, times(1)).uploadFile(any(MultipartFile.class), anyString());
        verify(employeeMapper, times(1)).updateSealImageUrl(employeeId, "new_seal_key");
    }

    @Test
    @DisplayName("이미지 직인 업로드 테스트")
    void uploadSealImageTest() {
        // given
        Integer employeeId = 1;
        MultipartFile file = mock(MultipartFile.class);
        given(file.isEmpty()).willReturn(false);
        given(file.getSize()).willReturn(1024L);

        given(employeeMapper.findSealImageUrlByEmployeeId(employeeId)).willReturn(null);
        given(s3Service.uploadFile(file, "seals")).willReturn("new_image_key");
        given(employeeMapper.updateSealImageUrl(employeeId, "new_image_key")).willReturn(1);

        // when
        sealService.uploadSealImage(employeeId, file);

        // then
        verify(s3Service, times(1)).uploadFile(file, "seals");
        verify(employeeMapper, times(1)).updateSealImageUrl(employeeId, "new_image_key");
    }

    @Test
    @DisplayName("직인 삭제 테스트")
    void deleteSealTest() {
        // given
        Integer employeeId = 1;
        given(employeeMapper.findSealImageUrlByEmployeeId(employeeId)).willReturn("seal.png");
        given(employeeMapper.updateSealImageUrl(employeeId, null)).willReturn(1);

        // when
        sealService.deleteSeal(employeeId);

        // then
        verify(s3Service, times(1)).deleteFile("seal.png");
        verify(employeeMapper, times(1)).updateSealImageUrl(employeeId, null);
    }
}
