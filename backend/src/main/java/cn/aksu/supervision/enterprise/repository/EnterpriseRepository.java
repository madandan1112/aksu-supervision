package cn.aksu.supervision.enterprise.repository;

import cn.aksu.supervision.enterprise.entity.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EnterpriseRepository extends JpaRepository<Enterprise, Long> {

    Optional<Enterprise> findByUserId(Long userId);

    Optional<Enterprise> findByCreditCode(String creditCode);

    @Query("SELECT e FROM Enterprise e WHERE " +
           "(:keyword IS NULL OR e.name LIKE %:keyword% OR e.creditCode LIKE %:keyword%) AND " +
           "(:industry IS NULL OR e.industry = :industry) AND " +
           "(:area IS NULL OR e.area = :area) AND " +
           "(:status IS NULL OR e.status = :status)")
    Page<Enterprise> findByConditions(@Param("keyword") String keyword,
                                      @Param("industry") String industry,
                                      @Param("area") String area,
                                      @Param("status") Integer status,
                                      Pageable pageable);
}
