package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Integer> {

    /**
     * 직급명으로 직급 엔티티 조회
     *
     * @param grade 직급명
     * @return Optional<Grade>
     */
    Optional<Grade> findByGrade(String grade);
}
