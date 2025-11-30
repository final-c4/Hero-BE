package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Integer> {
}
