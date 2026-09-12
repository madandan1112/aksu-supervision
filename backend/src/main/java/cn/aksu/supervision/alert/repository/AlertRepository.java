package cn.aksu.supervision.alert.repository;

import cn.aksu.supervision.alert.entity.Alert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

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

    /**
     * 去重查询：同一企业同一类型在指定时间窗口内是否已存在预警
     */
    @Query("SELECT COUNT(a) > 0 FROM Alert a WHERE a.enterpriseId = :enterpriseId " +
           "AND a.alertType = :alertType AND a.createTime >= :since " +
           "AND a.status IN ('PENDING', 'HANDLING', 'ESCALATED')")
    boolean existsDuplicate(@Param("enterpriseId") Long enterpriseId,
                            @Param("alertType") String alertType,
                            @Param("since") LocalDateTime since);

    /**
     * 去重查询（无企业归属的预警，如注册备案异常）：按标题+类型去重
     */
    @Query("SELECT COUNT(a) > 0 FROM Alert a WHERE a.enterpriseId IS NULL " +
           "AND a.alertType = :alertType AND a.title = :title AND a.createTime >= :since " +
           "AND a.status IN ('PENDING', 'HANDLING', 'ESCALATED')")
    boolean existsDuplicateByTitle(@Param("alertType") String alertType,
                                   @Param("title") String title,
                                   @Param("since") LocalDateTime since);

    /**
     * 查询需要督办升级的预警：状态为PENDING/HANDLING/ESCALATED且已过escalate_at时间
     */
    @Query("SELECT a FROM Alert a WHERE a.status IN ('PENDING', 'HANDLING', 'ESCALATED') " +
           "AND a.escalateAt IS NOT NULL AND a.escalateAt < :now " +
           "ORDER BY a.escalateAt ASC")
    List<Alert> findOverdueForEscalation(@Param("now") LocalDateTime now);

    /**
     * 查询某企业的所有未关闭预警
     */
    List<Alert> findByEnterpriseIdAndStatusIn(Long enterpriseId, List<String> statuses);

    /**
     * 统计某企业某类型的预警次数（用于屡次违规判断）
     */
    @Query("SELECT COUNT(a) FROM Alert a WHERE a.enterpriseId = :enterpriseId " +
           "AND a.alertType IN :types AND a.createTime >= :since")
    long countByEnterpriseAndTypesSince(@Param("enterpriseId") Long enterpriseId,
                                         @Param("types") List<String> types,
                                         @Param("since") LocalDateTime since);
}
