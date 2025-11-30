package com.c4.hero.domain.employee.entity;

import com.c4.hero.domain.employee.type.RoleType;
import com.c4.hero.domain.employee.type.RoleTypeConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_roles")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer roleId;

    @Convert(converter = RoleTypeConverter.class) // 컨버터 적용
    @Column(name = "role", length = 50)
    private RoleType role;
}
