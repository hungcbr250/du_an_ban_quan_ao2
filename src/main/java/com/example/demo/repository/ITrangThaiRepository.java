package com.example.demo.repository;

import com.example.demo.model.TrangThai;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITrangThaiRepository extends JpaRepository<TrangThai,Integer> {
    TrangThai findTrangThaiById(int idHangValue);
}
