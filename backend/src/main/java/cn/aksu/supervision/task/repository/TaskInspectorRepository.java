package cn.aksu.supervision.task.repository;

import cn.aksu.supervision.task.entity.TaskInspector;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskInspectorRepository extends JpaRepository<TaskInspector, Long> {

    List<TaskInspector> findByTaskId(Long taskId);

    Page<TaskInspector> findByInspectorIdAndStatusOrderByCreateTimeDesc(Long inspectorId, String status, Pageable pageable);

    Page<TaskInspector> findByInspectorIdOrderByCreateTimeDesc(Long inspectorId, Pageable pageable);

    long countByInspectorIdAndStatus(Long inspectorId, String status);

    long countByInspectorId(Long inspectorId);
}
