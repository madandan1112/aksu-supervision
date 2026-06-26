package cn.aksu.supervision.alert.repository;

import cn.aksu.supervision.alert.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    Page<Alert> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    @Query("SELECT a FROM Alert a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:level IS NULL OR a.level = :level) AND " +
           "(:type IS NULL OR a.alertType = :type)")
    Page<Alert> findByConditions(@Param("status") String status,
                                  @Param("level") String level,
                                  @Param("type") String type,
                                  Pageable pageable);

    long countByStatus(String status);

    long countByLevel(String level);

    Page<Alert> findByEnterpriseId(Long enterpriseId, Pageable pageable);
}
