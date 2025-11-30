package com.c4.hero.domain.employee.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_department")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Department {

    @Id
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "department_name", nullable = false, length = 100)
    private String departmentName;

    @Column(name = "department_phone", length = 20)
    private String departmentPhone;

    @Column(name = "depth", nullable = false)
    private Integer depth;

    @Column(name = "parent_department_id")
    private Integer parentDepartmentId;

    @Column(name = "manager_id")
    private Integer managerId;
}
