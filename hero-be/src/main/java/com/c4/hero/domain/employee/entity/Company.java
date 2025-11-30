package com.c4.hero.domain.employee.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_company")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Integer companyId;

    @Column(name = "company_name", length = 30)
    private String companyName;

    @Column(name = "business_no", length = 30)
    private String businessNo;

    @Column(name = "address")
    private String address;

    @Column(name = "ceo_name", length = 30)
    private String ceoName;

    @Column(name = "contact_phone", length = 20)
    private String contactPhone;

    @Column(name = "contact_email", length = 30)
    private String contactEmail;

    @Column(name = "established_date")
    private LocalDate establishedDate;

    @Column(name = "fax_number", length = 30)
    private String faxNumber;

    @Column(name = "logo_url")
    private String logoUrl;

    @Column(name = "seal_file_url")
    private String sealFileUrl;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "lunch_time")
    private LocalDateTime lunchTime;
}
