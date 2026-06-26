package cn.aksu.supervision.task.repository;

import cn.aksu.supervision.task.entity.TaskExtension;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskExtensionRepository extends JpaRepository<TaskExtension, Long> {

    List<TaskExtension> findByTaskIdOrderByCreateTimeDesc(Long taskId);

    List<TaskExtension> findByInspectorIdOrderByCreateTimeDesc(Long inspectorId);
}
