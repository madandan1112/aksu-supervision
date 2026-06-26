package cn.aksu.supervision.rectification.repository;

import cn.aksu.supervision.rectification.entity.AcceptanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AcceptanceRecordRepository extends JpaRepository<AcceptanceRecord, Long> {

    List<AcceptanceRecord> findByNoticeIdOrderByCreateTimeDesc(Long noticeId);
}
