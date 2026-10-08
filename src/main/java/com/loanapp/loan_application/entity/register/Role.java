package com.loanapp.loan_application.entity.register;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name ="Roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RoleId")
    private Long roleId;

    @Column(name = "RoleName", nullable = false, unique = true)
    private String roleName;
}
