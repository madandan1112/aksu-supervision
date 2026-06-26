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
           "(:keyword IS NULL OR r.title LIKE %:keyword%)")
    Page<ComplianceReport> findByConditions(@Param("status") String status,
                                             @Param("keyword") String keyword,
                                             Pageable pageable);

    @Query("SELECT r FROM ComplianceReport r WHERE r.expireDate BETWEEN :now AND :deadline AND r.status = 'APPROVED'")
    List<ComplianceReport> findExpiringReports(@Param("now") LocalDateTime now,
                                                @Param("deadline") LocalDateTime deadline);
}
