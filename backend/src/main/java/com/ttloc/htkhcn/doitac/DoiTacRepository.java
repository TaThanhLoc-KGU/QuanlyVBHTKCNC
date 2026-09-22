package com.ttloc.htkhcn.doitac;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DoiTacRepository extends JpaRepository<DoiTac, UUID>, JpaSpecificationExecutor<DoiTac> {

    Optional<DoiTac> findByIdAndDeletedAtIsNull(UUID id);

    /** Goi y doi tac trung/gan giong ten khi them moi hoac khi import Excel (SPEC 3.1, 4.3). */
    @Query(value = """
            SELECT d.*, similarity(fn_unaccent_lower(d.ten_doi_tac), fn_unaccent_lower(:ten)) AS do_giong
            FROM doi_tac d
            WHERE d.deleted_at IS NULL
              AND fn_unaccent_lower(d.ten_doi_tac) % fn_unaccent_lower(:ten)
            ORDER BY do_giong DESC
            LIMIT 5
            """, nativeQuery = true)
    List<DoiTac> timDoiTacGanGiong(@Param("ten") String ten);

    /** Diem giong (0-1) giua 1 doi tac cu the va 1 ten - dung de quyet dinh co
     * TU DONG gop khi import Excel khong (nguong chat hon nguong goi y '%'
     * o tren, tranh gop nham cac ten chi chung tien to nhu "Dai hoc ..."). */
    @Query(value = """
            SELECT similarity(fn_unaccent_lower(ten_doi_tac), fn_unaccent_lower(:ten))
            FROM doi_tac WHERE id = :id
            """, nativeQuery = true)
    Double diemGiongVoi(@Param("ten") String ten, @Param("id") java.util.UUID id);

    @Query(value = """
            SELECT * FROM doi_tac
            WHERE deleted_at IS NULL
              AND tsv @@ plainto_tsquery('simple', fn_unaccent_lower(:tuKhoa))
            """, nativeQuery = true)
    List<DoiTac> timKiemTheoTuKhoa(@Param("tuKhoa") String tuKhoa);
}
