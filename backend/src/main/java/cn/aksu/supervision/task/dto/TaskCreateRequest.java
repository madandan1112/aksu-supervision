package cn.aksu.supervision.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskCreateRequest {

    @NotBlank(message = "任务标题不能为空")
    private String title;

    private String description;

    @NotBlank(message = "任务类型不能为空")
    private String taskType;

    private LocalDateTime plannedStartTime;

    private LocalDateTime plannedEndTime;

    private String checkItems;

    private List<Long> enterpriseIds;

    private List<Long> inspectorIds;
}
