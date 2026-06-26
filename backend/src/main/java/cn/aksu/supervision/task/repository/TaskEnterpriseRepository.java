package cn.aksu.supervision.task.repository;

import cn.aksu.supervision.task.entity.TaskEnterprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskEnterpriseRepository extends JpaRepository<TaskEnterprise, Long> {

    List<TaskEnterprise> findByTaskId(Long taskId);

    List<TaskEnterprise> findByEnterpriseId(Long enterpriseId);
}
