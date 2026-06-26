package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgRequest {

    @NotBlank(message = "组织名称不能为空")
    private String name;

    private Long parentId;

    @NotNull(message = "层级不能为空")
    private Integer level;

    private Integer sortOrder;

    private Integer status;

    private String description;

    /** 组织编码 */
    private String orgCode;

    /** 组织类型：企业/机关/事业单位/社会团体 */
    private String orgType;

    /** 统一社会信用代码 */
    private String unifiedSocialCreditCode;

    /** 负责人姓名 */
    private String leaderName;

    /** 负责人电话 */
    private String leaderPhone;

    /** 地址 */
    private String address;

    /** 备注 */
    private String remark;
}