package com.example.demo.controller;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.IChiTietSanPhamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Controller

public class LoginController {
    @Autowired
    private IAppUserRepository appUserRepository;

    @Autowired
    private IUserRoleRepository iUserRoleRepository;
    @Autowired
    private IHangRepository repositoryHang;

    @Autowired
    private ILoaiRepository repositoryLoai;

    @Autowired
    private IMauSacRepository repositoryMauSac;

    @Autowired
    private IChiTietSanPhamRepository repositoryChiTiet;

    @Autowired
    private IChatLieuRepository repositoryChatLieu;

    @Autowired
    private IKichCoRepository repositoryKichCo;

    @Autowired
    private IChiTietSanPhamService serviceChiTiet;
    @Autowired
    private IGioHangRepository gioHangRepository;

    @Autowired
    private IGioHangChiTietRepository gioHangChiTietRepository;

    @Autowired
    private IGioHangChiTietSessionRepo iGioHangChiTietSessionRepo;
    @Autowired
    private IHoaDonRepository iHoaDonRepository;
    @Autowired
    private ITrangThaiOrderRepo iTrangThaiOrderRepo;
    @Autowired
    private IHoaDonChiTietRepo iHoaDonChiTietRepo;

    @RequestMapping("/login")
    public String showLogin() {
        return "login/login";
    }

    @PostMapping("/checkLogin")
    public String checkLogin(ModelMap modelMap,
                             @RequestParam(required = false, name = "email") String username,
                             @RequestParam(name = "matKhau") String password, Model model,
                             @RequestParam(defaultValue = "1") int page,
                             @RequestParam(required = false, name = "tenSanPham") String keyword,
                             @RequestParam(name = "min", defaultValue = "0") BigDecimal min,
                             @RequestParam(name = "max", defaultValue = "100000000") BigDecimal max,
                             @RequestParam(name = "idLoai", required = false) Integer loaiId
    ) {

        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            // Xử lý khi tên đăng nhập hoặc mật khẩu trống
            return "login/login";
        }

        AppUser appUser = appUserRepository.findAppUserByEmail(username);

        UserRole userRole = iUserRoleRepository.findUserRoleByAppUser(appUser);


        if (appUser != null) {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            if (passwordEncoder.matches(password, appUser.getEncrytedPassword())) {
                if (userRole.getAppRole().getRoleName().equals("ROLE_ADMIN")) {
                    // Xử lý đăng nhập cho admin
                    // Thực hiện các thao tác đăng nhập của admin
                    if (page < 1) {
                        page = 1;
                    }

                    Pageable pageable = PageRequest.of(page - 1, 6);
                    Page<ChitietSanPham> listCTSP;

                    if (loaiId != null) {
                        listCTSP = repositoryChiTiet.findByIdLoai(loaiId, pageable);
                    } else if (keyword == null || keyword.isBlank() && min == null && max == null) {
                        listCTSP = repositoryChiTiet.findAll(pageable);
                    } else if (keyword == null || keyword.isBlank()) {
                        listCTSP = repositoryChiTiet.findAll(pageable);

                    } else {
                        listCTSP = serviceChiTiet.searchByTenAndGiaRange(keyword, min, max, pageable);
                    }
                    model.addAttribute("listChiTietSanPham", listCTSP);

                    return "ban-hang/shop";

                } else if (userRole.getAppRole().getRoleName().equals("ROLE_USER")) {
                    Pageable pageable = PageRequest.of(page - 1, 6);
                    Page<ChitietSanPham> listCTSP;

                    // Xử lý đăng nhập cho khách hàng
                    // Thực hiện các thao tác đăng nhập của khách hàng
                    if (loaiId != null) {
                        listCTSP = repositoryChiTiet.findByIdLoai(loaiId, pageable);
                    } else if (keyword == null || keyword.isBlank() && min == null && max == null) {
                        listCTSP = repositoryChiTiet.findAll(pageable);
                    } else if (keyword == null || keyword.isBlank()) {
                        listCTSP = repositoryChiTiet.findAll(pageable);

                    } else {
                        listCTSP = serviceChiTiet.searchByTenAndGiaRange(keyword, min, max, pageable);
                    }
                    model.addAttribute("appUser", appUser);
                    model.addAttribute("listChiTietSanPham", listCTSP);
                    return "ban-hang/shop";

                }
            }
        }

        // Xử lý khi tên đăng nhập hoặc mật khẩu không đúng
        model.addAttribute("ERROR", "Username or password not exist");
        return "login/login";
    }

    @GetMapping("/cart/{userId}")
    private String cartUser(Model model,
                            @PathVariable(name = "userId") Long userId) {
        // tìm khach hang co id
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        // lấy ra gio hàng có id là khách hàng ở trêm
        GioHang gioHang = gioHangRepository.findGioHangByUserId(appUser);
        // lấy ghct
        List<GioHangChiTiet> gioHangChiTiets = gioHangChiTietRepository.findByIdGioHang(gioHang);
        model.addAttribute("gioHangChiTiets", gioHangChiTiets);
        model.addAttribute("appUser", appUser);
        return "ban-hang/cart";

    }

    @GetMapping("/my-order/{userId}")
    private String viewOrder(Model model,
                             @PathVariable(name = "userId") Long userId) {
        // tìm khach hang co id
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
       List<HoaDon> hoaDons = iHoaDonRepository.findHoaDonByUserId(appUser);
    //    List<HoaDon> hoaDons = iHoaDonRepository.findAll();


        model.addAttribute("hoaDon", hoaDons);
        model.addAttribute("appUser", appUser);
        return "hoa-don/hoa-don";

    }

    @PostMapping("/add-to-cart/{id}")
    private String addToCart(Model model, @PathVariable(name = "id") Integer id
            , @RequestParam(name = "userId") Long userId
    ) {
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        ChitietSanPham chitietSanPham = serviceChiTiet.findChitietSanPhamById(id);
        iGioHangChiTietSessionRepo.addToCart(chitietSanPham, userId);
        model.addAttribute("appUser", appUser);
        return "redirect:/cart/" + userId;

    }

    @PostMapping("/reduce-cart/{id}")
    private String giamSanPham(Model model, @PathVariable(name = "id") Integer id
            , @RequestParam(name = "userId") Long userId
    ) {
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        ChitietSanPham chitietSanPham = serviceChiTiet.findChitietSanPhamById(id);
        iGioHangChiTietSessionRepo.truSanPham(userId, chitietSanPham);
        model.addAttribute("appUser", appUser);
        return "redirect:/cart/" + userId;


    }

    @PostMapping("/remove-cart/{id}")
    private String xoaSanPham(Model model, @PathVariable(name = "id") Integer id
            , @RequestParam(name = "userId") Long userId
    ) {
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        ChitietSanPham chitietSanPham = serviceChiTiet.findChitietSanPhamById(id);
        iGioHangChiTietSessionRepo.xoaSanPham(userId, chitietSanPham);
        model.addAttribute("appUser", appUser);
        return "redirect:/cart/" + userId;


    }

    @PostMapping("/tao-hoa-don")
    private String taoHoaDon(Model model, @RequestParam(name = "userId") Long userId
            , @RequestParam(value = "totalPrice", required = false) BigDecimal totalPrice

    ) {
        Integer tt = 1;
        TrangThaiOrder trangThaiOrder = iTrangThaiOrderRepo.findById(tt).orElse(null);
        AppUser appUser = appUserRepository.findById(userId).orElse(null);
        GioHang gioHang = gioHangRepository.findGioHangByUserId(appUser);
        // lấy ghct
        List<GioHangChiTiet> gioHangChiTiets = gioHangChiTietRepository.findByIdGioHang(gioHang);
        HoaDon hoaDon = new HoaDon();
        hoaDon.setUserId(appUser);
        hoaDon.setIdTT(trangThaiOrder);
        hoaDon.setTongTien(totalPrice);
        LocalDateTime currentDateTime = LocalDateTime.now();
        hoaDon.setNgayDat(currentDateTime);
        iHoaDonRepository.save(hoaDon);

        Integer idHoaDonVuaTao = hoaDon.getId();
        HoaDon hoaDonIdVuaTao = iHoaDonRepository.findById(idHoaDonVuaTao).orElse(null);
        for (GioHangChiTiet gioHangChiTiet : gioHangChiTiets) {
            HoaDonChiTiet hoaDonChiTiet = new HoaDonChiTiet();
            hoaDonChiTiet.setIdHoaDon(hoaDonIdVuaTao);
            hoaDonChiTiet.setIdChiTietSanPham(gioHangChiTiet.getIdChiTietSanPham());
            hoaDonChiTiet.setSoLuong(gioHangChiTiet.getSoLuong());
            hoaDonChiTiet.setDonGia(gioHangChiTiet.getDonGia());
            iHoaDonChiTietRepo.save(hoaDonChiTiet);
        }
        model.addAttribute("hoaDon", hoaDonIdVuaTao);
        return "thanh-toan/checkout";
    }

    @PostMapping("/thanh-toan/{idHoaDon}")
    private String datHang(Model model
            , @PathVariable(name = "idHoaDon") Integer idHoaDon
            , @RequestParam(name = "sdt") String sdt
            , @RequestParam(name = "diaChi") String diaChi
            , @RequestParam(name = "tenNguoiNhan") String tenNguoiNhan
            , @RequestParam(name = "sdtNguoiNhan") String sdtNguoiNhan


    ) {
        HoaDon hoaDon = iHoaDonRepository.findById(idHoaDon).orElse(null);
        hoaDon.setSdt(sdt);
        hoaDon.setDiaChi(diaChi);
        hoaDon.setTenNguoiNhan(tenNguoiNhan);
        hoaDon.setSdtNguoiNhan(sdtNguoiNhan);
        iHoaDonRepository.save(hoaDon);

        AppUser appUser = appUserRepository.findById(hoaDon.getUserId().getUserId()).orElse(null);
        GioHang gioHang = gioHangRepository.findGioHangByUserId(appUser);
        List<GioHangChiTiet> gioHangChiTiet = gioHangChiTietRepository.findByIdGioHang(gioHang);
        gioHangChiTietRepository.deleteAll(gioHangChiTiet);
        model.addAttribute("successMessage", "Bạn đã đặt hàng thành công đơn hàng sẽ đến trong vài ngày nữa.");
        model.addAttribute("appUser", appUser);

        return "thanh-toan/thanh-cong";
    }
}
