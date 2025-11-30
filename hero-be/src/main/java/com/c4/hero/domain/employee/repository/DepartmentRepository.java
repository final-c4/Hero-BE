package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {

    /**
     * 부서 이름으로 부서 엔티티 조회
     *
     * @param departmentName 부서 이름
     * @return Optional<Department>
     */
    Optional<Department> findByDepartmentName(String departmentName);
}
