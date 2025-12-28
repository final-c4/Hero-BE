package com.c4.hero.domain.employee.service;

import com.c4.hero.domain.employee.dto.request.SealTextUpdateRequestDTO;
import com.c4.hero.domain.employee.mapper.EmployeeMapper;
import com.c4.hero.common.s3.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * <pre>
 * Class Name: EmployeeSealService
 * Description: 직인 관리 서비스
 *
 * History
 * 2025/12/28 (혜원) 최초 작성
 * </pre>
 *
 * @author 혜원
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmployeeSealService {

    private final EmployeeMapper employeeMapper;
    private final S3Service s3Service;

    private static final String SEAL_DIRECTORY = "seals";

    /**
     * 텍스트 직인 업데이트
     *
     * @param employeeId 직원 ID
     * @param requestDTO 텍스트 직인 정보
     */
    @Transactional
    public void updateSealText(Integer employeeId, SealTextUpdateRequestDTO requestDTO) {
        log.info("텍스트 직인 업데이트 시작 - employeeId: {}, text: {}", employeeId, requestDTO.getSealText());

        // 기존 이미지 직인이 있으면 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
            log.info("기존 이미지 직인 삭제 - employeeId: {}", employeeId);
        }

        // seal_image_url을 null로 설정 (텍스트 직인은 프론트에서 생성)
        int updated = employeeMapper.updateSealImageUrl(employeeId, null);

        if (updated == 0) {
            log.error("텍스트 직인 업데이트 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("텍스트 직인 업데이트에 실패했습니다.");
        }

        log.info("텍스트 직인 업데이트 성공 - employeeId: {}", employeeId);
    }

    /**
     * 이미지 직인 업로드
     *
     * @param employeeId 직원 ID
     * @param file 직인 이미지 파일
     */
    @Transactional
    public void uploadSealImage(Integer employeeId, MultipartFile file) {
        log.info("이미지 직인 업로드 시작 - employeeId: {}, filename: {}", employeeId, file.getOriginalFilename());

        // 파일 검증
        if (file.isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }

        // 파일 크기 검증 (5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("파일 크기는 5MB 이하여야 합니다.");
        }

        // 기존 이미지가 있으면 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
            log.info("기존 직인 이미지 삭제 - employeeId: {}", employeeId);
        }

        // S3에 업로드 (키 반환)
        String s3Key = s3Service.uploadFile(file, SEAL_DIRECTORY);

        // DB에 S3 키 저장
        int updated = employeeMapper.updateSealImageUrl(employeeId, s3Key);

        if (updated == 0) {
            // 업데이트 실패 시 S3에서도 삭제
            s3Service.deleteFile(s3Key);
            log.error("이미지 직인 업데이트 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("이미지 직인 업데이트에 실패했습니다.");
        }

        log.info("이미지 직인 업로드 성공 - employeeId: {}, S3 Key: {}", employeeId, s3Key);
    }

    /**
     * 직인 삭제
     *
     * @param employeeId 직원 ID
     */
    @Transactional
    public void deleteSeal(Integer employeeId) {
        log.info("직인 삭제 시작 - employeeId: {}", employeeId);

        // 기존 이미지가 있으면 S3에서 삭제
        String existingSealUrl = employeeMapper.findSealImageUrlByEmployeeId(employeeId);
        if (existingSealUrl != null && !existingSealUrl.isEmpty()) {
            s3Service.deleteFile(existingSealUrl);
        }

        // DB에서 삭제
        int updated = employeeMapper.updateSealImageUrl(employeeId, null);

        if (updated == 0) {
            log.error("직인 삭제 실패 - employeeId: {}", employeeId);
            throw new RuntimeException("직인 삭제에 실패했습니다.");
        }

        log.info("직인 삭제 성공 - employeeId: {}", employeeId);
    }
}