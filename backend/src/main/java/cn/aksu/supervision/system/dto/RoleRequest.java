package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RoleRequest {

    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    private String description;

    /** Data scope: ALL/DEPT/SELF/CUSTOM */
    private String dataScope;

    /** Region scope: ALL/REGION/CITY/COUNTY/SELF */
    private String regionScope;

    /** Dept scope: ALL/DEPT/SELF */
    private String deptScope;

    /** Org level: REGION/CITY/COUNTY/DEPT/ENTERPRISE */
    private String orgLevel;

    /** Parent role ID */
    private Long parentId;

    /** Role type: ADMIN/SECURITY/AUDIT/BUSINESS */
    private String roleType;

    /** Sort order */
    private Integer sortOrder;

    /** Status */
    private Integer status;
}
