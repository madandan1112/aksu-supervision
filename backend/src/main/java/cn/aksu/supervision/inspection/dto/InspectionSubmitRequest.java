package cn.aksu.supervision.inspection.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InspectionSubmitRequest {

    private Long taskId;

    @NotNull(message = "企业ID不能为空")
    private Long enterpriseId;

    private String formData;

    private String issues;

    private String requirements;

    private String deadline;

    private String checkTypes;

    private String evidenceImages;

    private String evidenceVideos;

    private String attachmentUrls;

    private String remark;

    private String summary;
}
