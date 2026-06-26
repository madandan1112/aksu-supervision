package cn.aksu.supervision.rectification.repository;

import cn.aksu.supervision.rectification.entity.RectificationNotice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RectificationNoticeRepository extends JpaRepository<RectificationNotice, Long> {

    Page<RectificationNotice> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);

    Page<RectificationNotice> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    Page<RectificationNotice> findByInspectorIdOrderByCreateTimeDesc(Long inspectorId, Pageable pageable);
}
