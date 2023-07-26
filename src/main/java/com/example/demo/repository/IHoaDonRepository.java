package com.example.demo.repository;

import com.example.demo.model.AppUser;
import com.example.demo.model.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IHoaDonRepository extends JpaRepository<HoaDon,Integer> {
    @Query("select hd from HoaDon hd order by hd.idTT.id ")
    List<HoaDon> findAll();
    @Query("select hd from HoaDon hd order by hd.ngayDat desc")
    List<HoaDon> findHoaDonByUserId(AppUser appUser);


}
