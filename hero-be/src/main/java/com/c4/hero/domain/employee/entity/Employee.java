package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.EmployeeStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_employee")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "employee_id")
    private Integer employeeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "employee_number", unique = true, nullable = false, length = 50)
    private String employeeNumber;

    @Column(name = "employee_name", nullable = false, length = 50)
    private String employeeName;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "varbinary(2048)")
    private byte[] email;

    @Column(name = "phone", unique = true, nullable = false, columnDefinition = "varbinary(128)")
    private byte[] phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender", nullable = false, length = 50)
    private String gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EmployeeStatus status;

    @Column(name = "contract_type", nullable = false, length = 50)
    private String contractType;

    @Column(name = "address", columnDefinition = "varbinary(4096)")
    private byte[] address;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "termination_date")
    private LocalDate terminationDate;

    @Column(name = "retention_expire_at")
    private LocalDate retentionExpireAt;

    @Column(name = "image_path", nullable = false)
    private String imagePath;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "seal_image_url")
    private String sealImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position")
    private Grade grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_title")
    private JobTitle jobTitle;


    @Builder
    public Employee(Department department, String employeeNumber, String employeeName, byte[] email, byte[] phone, LocalDate birthDate, String gender, EmployeeStatus status, String contractType, byte[] address, LocalDate hireDate, String imagePath, Grade grade, JobTitle jobTitle) {
        this.department = department;
        this.employeeNumber = employeeNumber;
        this.employeeName = employeeName;
        this.email = email;
        this.phone = phone;
        this.birthDate = birthDate;
        this.gender = gender;
        this.status = status;
        this.contractType = contractType;
        this.address = address;
        this.hireDate = hireDate;
        this.imagePath = imagePath;
        this.grade = grade;
        this.jobTitle = jobTitle;
    }
}
