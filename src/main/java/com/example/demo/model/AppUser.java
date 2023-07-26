package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "appuser")
@Entity
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userid")
    private Long userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "Encryted_Password")
    private String encrytedPassword;

    @Column(name = "Enabled")
    private boolean enabled;

    @Column(name = "Email")
    private String email;

    @Column(name = "fullname")
    private String fullName;

    @Column(name = "ngaysinh")
    private String ngaySinh;

    @Column(name = "gioitinh")
    private String gioiTinh;
    @Column(name = "diachi")
    private String diaChi;
}
