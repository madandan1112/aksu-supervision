package cn.aksu.supervision.report.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportUploadRequest {

    @NotBlank(message = "报告标题不能为空")
    private String title;

    private String reportType;

    @NotBlank(message = "文件地址不能为空")
    private String fileUrl;

    private String fileName;

    @NotNull(message = "有效期不能为空")
    private LocalDateTime expireDate;
}
