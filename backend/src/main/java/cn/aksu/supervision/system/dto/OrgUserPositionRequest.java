package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class OrgUserPositionRequest {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "组织ID不能为空")
    private Long orgId;

    private Long positionId;

    private Long jobTitleId;

    /** 是否主职：1主职,0兼职 */
    private Integer isPrimary;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer status;
}