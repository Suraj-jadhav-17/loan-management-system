package com.loanapp.loan_application.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name="Users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "roleId",nullable = false)
    private Role role;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId",unique = true)
    private Customer customer;
    @Column(name = "FirstName")
    private String firstName;
    @Column(name = "LastName")
    private String lastName;
    @Column(name = "Email")
    private String email;
    @Column(name = "Mobile")
    private String mobileNo;
    @Column(name = "Password")
    private String password;
}
