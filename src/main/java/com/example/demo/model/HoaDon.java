package com.example.demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "hoadon")
@Entity
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "userid")
    private AppUser UserId;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "diachi")
    private String diaChi;

    @ManyToOne
    @JoinColumn(name = "idtt")
    private TrangThaiOrder idTT;

    @Column(name = "ngaydat")
    private LocalDateTime ngayDat;

    @Column(name = "ngayship")
    private LocalDateTime ngayShip;

    @Column(name = "ngaykhachnhan")
    private LocalDateTime ngayKhachNhan;

    @Column(name = "tongtien")
    private BigDecimal tongTien;

    @Column(name = "tennguoinhan")
    private String tenNguoiNhan;

    @Column(name = "sdtnguoinhan")
    private String sdtNguoiNhan;
}
