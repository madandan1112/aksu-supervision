package cn.aksu.supervision.appeal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AppealCreateRequest {

    @NotBlank(message = "诉求类型不能为空")
    private String appealType;

    @NotBlank(message = "标题不能为空")
    private String title;

    @NotBlank(message = "内容不能为空")
    private String content;

    private String relatedFields;

    private String attachments;
}
