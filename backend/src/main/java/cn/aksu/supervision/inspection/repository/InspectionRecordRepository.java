package cn.aksu.supervision.inspection.repository;

import cn.aksu.supervision.inspection.entity.InspectionRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionRecordRepository extends JpaRepository<InspectionRecord, Long> {

    Page<InspectionRecord> findByInspectorIdOrderByCreateTimeDesc(Long inspectorId, Pageable pageable);

    Page<InspectionRecord> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);

    Page<InspectionRecord> findByTaskIdOrderByCreateTimeDesc(Long taskId, Pageable pageable);

    long countByInspectorId(Long inspectorId);
}
