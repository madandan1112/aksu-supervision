package cn.aksu.supervision.report.repository;

import cn.aksu.supervision.report.entity.ComplianceReport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ComplianceReportRepository extends JpaRepository<ComplianceReport, Long> {

    Page<ComplianceReport> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);

    @Query("SELECT r FROM ComplianceReport r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:keyword IS NULL OR r.title LIKE CONCAT('%', :keyword, '%') OR " +
           " r.enterpriseId IN (SELECT e.id FROM Enterprise e WHERE e.name LIKE CONCAT('%', :keyword, '%')))")
    Page<ComplianceReport> findByConditions(@Param("status") String status,
                                              @Param("keyword") String keyword,
                                              Pageable pageable);

    @Query("SELECT r FROM ComplianceReport r WHERE r.expireDate BETWEEN :now AND :deadline AND r.status = 'APPROVED'")
    List<ComplianceReport> findExpiringReports(@Param("now") LocalDateTime now,
                                                 @Param("deadline") LocalDateTime deadline);

    @Query("SELECT r FROM ComplianceReport r WHERE r.enterpriseId = :enterpriseId AND r.expireDate BETWEEN :now AND :deadline AND r.status = 'APPROVED'")
    List<ComplianceReport> findExpiringReportsByEnterpriseId(@Param("enterpriseId") Long enterpriseId,
                                                              @Param("now") LocalDateTime now,
                                                              @Param("deadline") LocalDateTime deadline);

    @Query("SELECT r FROM ComplianceReport r WHERE r.enterpriseId = :enterpriseId AND r.expireDate < :now AND r.status = 'APPROVED'")
    List<ComplianceReport> findExpiredReportsByEnterpriseId(@Param("enterpriseId") Long enterpriseId,
                                                            @Param("now") LocalDateTime now);

    /** 全量已过期报告（预警引擎批量扫描用，避免逐企业 N+1 查询） */
    @Query("SELECT r FROM ComplianceReport r WHERE r.expireDate < :now AND r.status = 'APPROVED'")
    List<ComplianceReport> findExpiredReports(@Param("now") LocalDateTime now);
}
