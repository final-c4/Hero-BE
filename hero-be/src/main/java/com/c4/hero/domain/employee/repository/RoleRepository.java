package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Role;
import com.c4.hero.domain.employee.type.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    /**
     * 권한 타입으로 Role 엔티티 조회
     *
     * @param roleType 권한 타입 (e.g., RoleType.EMPLOYEE)
     * @return Optional<Role>
     */
    Optional<Role> findByRole(RoleType roleType);
}
