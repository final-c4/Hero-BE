package com.c4.hero.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_job_title")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JobTitle {

    @Id
    @Column(name = "job_title_id")
    private Integer jobTitleId;

    @Column(name = "job_title", nullable = false, length = 50)
    private String jobTitle;
}
