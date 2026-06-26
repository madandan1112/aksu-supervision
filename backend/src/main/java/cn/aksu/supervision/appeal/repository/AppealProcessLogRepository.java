package cn.aksu.supervision.appeal.repository;

import cn.aksu.supervision.appeal.entity.AppealProcessLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppealProcessLogRepository extends JpaRepository<AppealProcessLog, Long> {

    List<AppealProcessLog> findByAppealIdOrderByCreateTimeDesc(Long appealId);
}
