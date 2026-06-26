package cn.aksu.supervision.rectification.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AcceptanceRequest {

    @NotBlank(message = "验收结论不能为空")
    private String conclusion;

    private String opinion;

    private String evidenceImages;
}
