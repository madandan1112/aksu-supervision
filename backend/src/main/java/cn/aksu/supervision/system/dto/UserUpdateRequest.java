package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String realName;
    private String phone;
    private String email;
    private String userType;
    private Integer status;
    private String avatar;

    // 组织架构
    private Long orgId;
    private String orgName;

    // 岗位
    private Long positionId;
    private String positionName;

    // 职务
    private Long jobTitleId;
    private String jobTitleName;

    // 角色
    private Long roleId;
}
