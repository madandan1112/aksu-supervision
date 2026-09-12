package cn.aksu.supervision.rectification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackSubmitRequest {

    @NotBlank(message = "整改措施不能为空")
    private String rectifyMeasures;

    private String evidenceImages;

    private String evidenceVideos;

    private String remark;

    private String checkType;

    private String attachmentUrls;
}
