package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Integer> {
}
