package com.ttloc.htkhcn.dashboard;

import java.time.LocalDate;

import org.hibernate.annotations.Immutable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Doc tu materialized view mv_mou_den_han_theo_thang (V14) - bieu do 12 thang toi. */
@Entity
@Table(name = "mv_mou_den_han_theo_thang")
@Immutable
@Getter
@Setter
@NoArgsConstructor
public class MouDenHanTheoThang {

    @Id
    @Column(name = "thang")
    private LocalDate thang;

    @Column(name = "so_luong_mou_den_han")
    private Long soLuongMouDenHan;
}
