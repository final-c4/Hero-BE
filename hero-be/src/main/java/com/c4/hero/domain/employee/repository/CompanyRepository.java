package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Integer> {
}
