package cn.aksu.supervision.enterprise.repository;

import cn.aksu.supervision.enterprise.entity.Enterprise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
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

    /**
     * 带区域权限过滤的企业列表查询
     * accessibleAreas: 用户可访问的区域名称列表，企业area字段必须在该列表中
     */
    @Query("SELECT e FROM Enterprise e WHERE " +
           "(:keyword IS NULL OR e.name LIKE %:keyword% OR e.creditCode LIKE %:keyword%) AND " +
           "(:industry IS NULL OR e.industry = :industry) AND " +
           "(:area IS NULL OR e.area = :area) AND " +
           "(:status IS NULL OR e.status = :status) AND " +
           "e.area IN :accessibleAreas")
    Page<Enterprise> findByConditionsAndAreas(@Param("keyword") String keyword,
                                               @Param("industry") String industry,
                                               @Param("area") String area,
                                               @Param("status") Integer status,
                                               @Param("accessibleAreas") List<String> accessibleAreas,
                                               Pageable pageable);

    /** 已注销企业（信用异常规则批量扫描用） */
    List<Enterprise> findByStatus(Integer status);

    /** 已关闭企业（信用异常规则批量扫描用） */
    List<Enterprise> findByRegistrationStatus(String registrationStatus);
}
