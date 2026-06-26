package cn.aksu.supervision.task.repository;

import cn.aksu.supervision.task.entity.InspectionTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InspectionTaskRepository extends JpaRepository<InspectionTask, Long> {

    @Query("SELECT t FROM InspectionTask t WHERE " +
           "(:status IS NULL OR t.status = :status) AND " +
           "(:type IS NULL OR t.taskType = :type) AND " +
           "(:keyword IS NULL OR t.title LIKE %:keyword%)")
    Page<InspectionTask> findByConditions(@Param("status") String status,
                                           @Param("type") String type,
                                           @Param("keyword") String keyword,
                                           Pageable pageable);

    Page<InspectionTask> findByCreatedByOrderByCreateTimeDesc(String createdBy, Pageable pageable);

    long countByStatus(String status);
}
