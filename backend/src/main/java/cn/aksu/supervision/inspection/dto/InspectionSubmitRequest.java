package cn.aksu.supervision.inspection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InspectionSubmitRequest {

    @NotNull(message = "任务ID不能为空")
    private Long taskId;

    @NotNull(message = "企业ID不能为空")
    private Long enterpriseId;

    private String formData;

    private String issues;

    private String evidenceImages;

    private String evidenceVideos;

    private String summary;
}
