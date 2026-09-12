package cn.aksu.supervision.task.service;

import cn.aksu.supervision.common.BusinessException;
import cn.aksu.supervision.common.PageResult;
import cn.aksu.supervision.task.dto.TaskCreateRequest;
import cn.aksu.supervision.task.entity.InspectionTask;
import cn.aksu.supervision.task.entity.TaskEnterprise;
import cn.aksu.supervision.task.entity.TaskExtension;
import cn.aksu.supervision.task.entity.TaskInspector;
import cn.aksu.supervision.task.repository.InspectionTaskRepository;
import cn.aksu.supervision.task.repository.TaskEnterpriseRepository;
import cn.aksu.supervision.task.repository.TaskExtensionRepository;
import cn.aksu.supervision.task.repository.TaskInspectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class TaskService {

    private final InspectionTaskRepository taskRepository;
    private final TaskEnterpriseRepository taskEnterpriseRepository;
    private final TaskInspectorRepository taskInspectorRepository;
    private final TaskExtensionRepository taskExtensionRepository;

    public InspectionTask createTask(TaskCreateRequest request, String createdBy) {
        InspectionTask task = InspectionTask.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .taskType(request.getTaskType())
                .status("PENDING")
                .plannedStartTime(request.getPlannedStartTime())
                .plannedEndTime(request.getPlannedEndTime())
                .checkItems(request.getCheckItems())
                .createdBy(createdBy)
                .build();

        taskRepository.save(task);

        if (request.getEnterpriseIds() != null) {
            for (Long enterpriseId : request.getEnterpriseIds()) {
                TaskEnterprise te = TaskEnterprise.builder()
                        .taskId(task.getId())
                        .enterpriseId(enterpriseId)
                        .status("PENDING")
                        .build();
                taskEnterpriseRepository.save(te);
            }
        }

        if (request.getInspectorIds() != null) {
            for (Long inspectorId : request.getInspectorIds()) {
                TaskInspector ti = TaskInspector.builder()
                        .taskId(task.getId())
                        .inspectorId(inspectorId)
                        .status("ASSIGNED")
                        .build();
                taskInspectorRepository.save(ti);
            }
        }

        return task;
    }

    @Transactional(readOnly = true)
    public PageResult<InspectionTask> listTasks(String status, String type, String keyword, int page, int size) {
        Page<InspectionTask> pageData = taskRepository.findByConditions(status, type, keyword,
                PageRequest.of(page - 1, size));
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    @Transactional(readOnly = true)
    public InspectionTask getTask(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));
    }

    public InspectionTask updateTask(Long id, TaskCreateRequest request) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setTaskType(request.getTaskType());
        task.setPlannedStartTime(request.getPlannedStartTime());
        task.setPlannedEndTime(request.getPlannedEndTime());
        task.setCheckItems(request.getCheckItems());

        return taskRepository.save(task);
    }

    public InspectionTask terminateTask(Long id, String reason) {
        InspectionTask task = taskRepository.findById(id)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        task.setStatus("TERMINATED");
        task.setTerminateReason(reason);

        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public PageResult<TaskInspector> listInspectorTasks(Long inspectorId, String status, int page, int size) {
        Page<TaskInspector> pageData;
        if (status != null) {
            pageData = taskInspectorRepository.findByInspectorIdAndStatusOrderByCreateTimeDesc(
                    inspectorId, status, PageRequest.of(page - 1, size));
        } else {
            pageData = taskInspectorRepository.findByInspectorIdOrderByCreateTimeDesc(
                    inspectorId, PageRequest.of(page - 1, size));
        }
        return PageResult.of(pageData.getContent(), pageData.getTotalElements(), page, size);
    }

    public TaskInspector claimTask(Long taskId, Long inspectorId) {
        List<TaskInspector> inspectors = taskInspectorRepository.findByTaskId(taskId);
        TaskInspector taskInspector = inspectors.stream()
                .filter(ti -> ti.getInspectorId().equals(inspectorId))
                .findFirst()
                .orElseThrow(() -> new BusinessException("您不是此任务的检查员"));

        if ("CLAIMED".equals(taskInspector.getStatus())) {
            throw new BusinessException("任务已认领");
        }

        taskInspector.setStatus("CLAIMED");
        taskInspector.setClaimTime(LocalDateTime.now());

        InspectionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));
        if ("PENDING".equals(task.getStatus())) {
            task.setStatus("IN_PROGRESS");
            task.setActualStartTime(LocalDateTime.now());
            taskRepository.save(task);
        }

        return taskInspectorRepository.save(taskInspector);
    }

    public TaskExtension requestExtension(Long taskId, Long inspectorId,
                                           LocalDateTime requestedEndTime, String reason) {
        InspectionTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("任务不存在"));

        TaskExtension extension = TaskExtension.builder()
                .taskId(taskId)
                .inspectorId(inspectorId)
                .originalEndTime(task.getPlannedEndTime())
                .requestedEndTime(requestedEndTime)
                .reason(reason)
                .status("PENDING")
                .build();

        return taskExtensionRepository.save(extension);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getInspectorStatistics(Long inspectorId) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", taskInspectorRepository.countByInspectorId(inspectorId));
        stats.put("assigned", taskInspectorRepository.countByInspectorIdAndStatus(inspectorId, "ASSIGNED"));
        stats.put("claimed", taskInspectorRepository.countByInspectorIdAndStatus(inspectorId, "CLAIMED"));
        stats.put("completed", taskInspectorRepository.countByInspectorIdAndStatus(inspectorId, "COMPLETED"));
        return stats;
    }
}
