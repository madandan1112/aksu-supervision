package cn.aksu.supervision.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentReportRequest {

    @NotBlank(message = "上报类型不能为空")
    private String reportType;

    @NotBlank(message = "设备状态不能为空")
    private String status;

    private String content;

    private String images;
}
