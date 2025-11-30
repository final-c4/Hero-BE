package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.EmployeeDepartmentHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDepartmentHistoryRepository extends JpaRepository<EmployeeDepartmentHistory, Integer> {
}
