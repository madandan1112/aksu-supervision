package cn.aksu.supervision.rectification.repository;

import cn.aksu.supervision.rectification.entity.RectificationFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RectificationFeedbackRepository extends JpaRepository<RectificationFeedback, Long> {

    List<RectificationFeedback> findByNoticeIdOrderByCreateTimeDesc(Long noticeId);

    List<RectificationFeedback> findByEnterpriseIdOrderByCreateTimeDesc(Long enterpriseId);
}
