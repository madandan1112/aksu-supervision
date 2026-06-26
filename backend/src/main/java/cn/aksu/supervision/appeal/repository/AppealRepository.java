package cn.aksu.supervision.appeal.repository;

import cn.aksu.supervision.appeal.entity.Appeal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppealRepository extends JpaRepository<Appeal, Long> {

    Page<Appeal> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);

    @Query("SELECT a FROM Appeal a WHERE " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:type IS NULL OR a.appealType = :type) AND " +
           "(:keyword IS NULL OR a.title LIKE %:keyword%)")
    Page<Appeal> findByConditions(@Param("status") String status,
                                   @Param("type") String type,
                                   @Param("keyword") String keyword,
                                   Pageable pageable);

    long countByStatus(String status);

    long countByEnterpriseId(Long enterpriseId);

    @Query("SELECT COUNT(a) FROM Appeal a WHERE a.satisfaction >= 4")
    long countSatisfied();
}
