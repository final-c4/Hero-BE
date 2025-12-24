package com.c4.hero.domain.department.service;

import com.c4.hero.domain.department.dto.DepartmentDTO;
import com.c4.hero.domain.department.repository.DepartmentRepository;
import com.c4.hero.domain.employee.entity.EmployeeDepartment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <pre>
 * Class Name: DepartmentService
 * Description: 부서(Department) 도메인 관련 비즈니스 로직을 처리하는 서비스
 *
 * History
 * 2025/12/24 (이지윤) 최초 작성 및 백엔드 코딩 컨벤션 적용
 * </pre>
 *
 * 부서 엔티티(EmployeeDepartment)를 조회하여
 * 화면/클라이언트에 필요한 형태의 DepartmentDTO로 변환하는 역할을 담당합니다.
 *
 * 주 사용처:
 * - 공통 부서 드롭다운
 * - 근태/휴가/평가 등에서의 부서 필터링 옵션
 *
 * @author 이지윤
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class DepartmentService {

    /** 부서(직원-부서 매핑 포함) 조회를 위한 레포지토리 */
    private final DepartmentRepository departmentRepository;

    /**
     * 전체 부서 목록을 조회하여 DepartmentDTO 리스트로 반환합니다.
     *
     * <p>특징</p>
     * <ul>
     *     <li>현재는 모든 부서를 단순 조회하여 ID/이름만 반환</li>
     *     <li>추후 사용 여부(Y/N), 정렬 순서, 상위/하위 조직 구조 등이 필요하면 이 계층에서 가공</li>
     * </ul>
     *
     * @return 부서 정보 DTO 리스트
     */
    public List<DepartmentDTO> getDepartments() {
        List<EmployeeDepartment> departments = departmentRepository.findAll();

        return departments.stream()
                .map(d -> new DepartmentDTO(d.getDepartmentId(), d.getDepartmentName()))
                .toList();
    }
}
