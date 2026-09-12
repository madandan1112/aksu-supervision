package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobTitleRequest {

    @NotBlank(message = "职务编码不能为空")
    private String titleCode;

    @NotBlank(message = "职务名称不能为空")
    private String titleName;

    /** 类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政 */
    private String category;

    /** 是否领导职务：1是,0否 */
    private Integer isLeadership;

    private Integer level;

    private Integer status;

    private Integer sortOrder;

    /** 关联角色ID */
    private Long roleId;

    private String description;
}