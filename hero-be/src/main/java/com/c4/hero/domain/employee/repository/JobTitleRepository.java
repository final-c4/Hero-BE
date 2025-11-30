package com.c4.hero.domain.employee.repository;

import com.c4.hero.domain.employee.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface JobTitleRepository extends JpaRepository<JobTitle, Integer> {

    /**
     * 직책명으로 직책 엔티티 조회
     *
     * @param jobTitle 직책명
     * @return Optional<JobTitle>
     */
    Optional<JobTitle> findByJobTitle(String jobTitle);
}
