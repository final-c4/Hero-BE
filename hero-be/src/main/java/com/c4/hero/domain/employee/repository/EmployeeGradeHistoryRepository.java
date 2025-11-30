package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.EmployeeGradeHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeGradeHistoryRepository extends JpaRepository<EmployeeGradeHistory, Integer> {
}
