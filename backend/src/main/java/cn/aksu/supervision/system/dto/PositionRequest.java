package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionRequest {

    @NotBlank(message = "岗位编码不能为空")
    private String positionCode;

    @NotBlank(message = "岗位名称不能为空")
    private String positionName;

    /** 类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政 */
    private String category;

    private BigDecimal salaryRangeMin;

    private BigDecimal salaryRangeMax;

    /** 所属组织ID */
    private Long orgId;

    private Integer level;

    private Integer status;

    private Integer sortOrder;

    private String description;
}