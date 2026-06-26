package cn.aksu.supervision.equipment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentAddRequest {

    @NotBlank(message = "设备名称不能为空")
    private String name;

    private String model;

    private String manufacturer;

    private String location;

    private LocalDateTime installDate;

    private LocalDateTime nextInspectionDate;

    private String certificateNo;

    private String description;
}
