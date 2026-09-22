package com.ttloc.htkhcn.crawlphapluat;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Tu khoa dung de loc van ban khi crawl - Admin quan ly (them/tat) vi pham vi
 * hop tac KHCN & QHQT kha rong, can tinh chinh dan theo thoi gian. */
@Entity
@Table(name = "tu_khoa_crawl_phap_luat")
@Getter
@Setter
@NoArgsConstructor
public class TuKhoaCrawlPhapLuat {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tu_khoa", nullable = false, unique = true)
    private String tuKhoa;

    @Column(name = "hoat_dong", nullable = false)
    private boolean hoatDong = true;

    @Column(name = "ngay_tao", nullable = false)
    private OffsetDateTime ngayTao;
}
