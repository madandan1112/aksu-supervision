package cn.aksu.supervision.rectification.repository;

import cn.aksu.supervision.rectification.entity.RectificationNotice;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RectificationNoticeRepository extends JpaRepository<RectificationNotice, Long> {

    Page<RectificationNotice> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId, Pageable pageable);

    Page<RectificationNotice> findByStatusOrderByCreateTimeDesc(String status, Pageable pageable);

    Page<RectificationNotice> findByInspectorIdOrderByCreateTimeDesc(Long inspectorId, Pageable pageable);

    Page<RectificationNotice> findAllByOrderByCreateTimeDesc(Pageable pageable);

    Page<RectificationNotice> findByEnterpriseIdAndStatusOrderByCreateTimeDesc(Long enterpriseId, String status, Pageable pageable);

    List<RectificationNotice> findByEnterpriseIdAndStatusNot(Long enterpriseId, String status);

    /** 未完结通知批量查询（整改超期规则用） */
    List<RectificationNotice> findByStatusNot(String status);

    /** 计数版（屡次违规规则用，避免拉全量列表） */
    long countByEnterpriseIdAndStatusNot(Long enterpriseId, String status);

    /**
     * 关闭态集合查询：验收通过(APPROVE后为ACCEPTED)与历史COMPLETED均视为已关闭。
     * 预警引擎/企业状态派生/屡次违规均以此为准，避免口径分裂。
     */
    List<RectificationNotice> findByStatusNotIn(List<String> statuses);
    List<RectificationNotice> findByEnterpriseIdAndStatusNotIn(Long enterpriseId, List<String> statuses);
    long countByEnterpriseIdAndStatusNotIn(Long enterpriseId, List<String> statuses);
    long countByStatusIn(List<String> statuses);
}
