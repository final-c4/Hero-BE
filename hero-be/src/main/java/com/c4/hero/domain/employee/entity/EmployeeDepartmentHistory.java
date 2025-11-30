package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.ChangeType;
import com.c4.hero.domain.employee.type.ChangeTypeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_employee_department_history")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmployeeDepartmentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_history_id")
    private Integer employeeHistoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "changed_by") // TODO: Account 엔티티와 매핑 필요
    private Integer changedBy;

    @CreationTimestamp
    @Column(name = "changed_at", nullable = false, updatable = false)
    private LocalDateTime changedAt;

    @Convert(converter = ChangeTypeConverter.class)
    @Column(name = "change_type", nullable = false, length = 50)
    private ChangeType changeType;

    @Column(name = "department_name", nullable = false, length = 20)
    private String departmentName;

    @Builder
    public EmployeeDepartmentHistory(Employee employee, Integer changedBy, ChangeType changeType, String departmentName) {
        this.employee = employee;
        this.changedBy = changedBy;
        this.changeType = changeType;
        this.departmentName = departmentName;
    }
}
