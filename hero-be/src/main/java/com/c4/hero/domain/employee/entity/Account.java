package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.AccountStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_account")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Integer accountId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Column(name = "account", nullable = false, length = 20)
    private String account;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "account_status", nullable = false, length = 20)
    private AccountStatus accountStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "password_change_required", nullable = false)
    private boolean passwordChangeRequired;

    // AccountRole과의 OneToMany 관계 추가
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountRole> accountRoles = new ArrayList<>();

    @Builder
    public Account(Employee employee, String account, String passwordHash, AccountStatus accountStatus, boolean passwordChangeRequired) {
        this.employee = employee;
        this.account = account;
        this.passwordHash = passwordHash;
        this.accountStatus = accountStatus;
        this.passwordChangeRequired = passwordChangeRequired;
    }
}
