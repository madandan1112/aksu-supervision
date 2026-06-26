package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PermissionRequest {

    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    private String permissionType;
    private String module;
    private String url;
    private String method;
    private String parentId;
    private Integer sortOrder;
}
