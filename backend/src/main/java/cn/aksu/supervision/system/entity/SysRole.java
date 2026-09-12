package cn.aksu.supervision.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_role")
public class SysRole {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String roleCode;

    @Column(nullable = false, length = 100)
    private String roleName;

    @Column(length = 500)
    private String description;

    /** 父角色ID（角色继承） */
    @Column(name = "parent_id")
    private Long parentId;

    /** 角色类型：ADMIN系统管理员/SECURITY安全管理员/AUDIT审计管理员/BUSINESS业务角色 */
    @Column(name = "role_type", length = 20)
    private String roleType;

    /** 数据权限范围：ALL全部/DEPT本部门/SELF仅自己/CUSTOM自定义 */
    @Column(name = "data_scope", length = 20)
    @Builder.Default
    private String dataScope = "ALL";

    /** 区域数据权限范围：ALL-全部, REGION-地区级, CITY-市级, COUNTY-县级 */
    @Column(name = "region_scope", length = 20)
    @Builder.Default
    private String regionScope = "ALL";

    /** 科室数据权限范围：ALL-全部, SELF-仅自己, DEPT-本科室 */
    @Column(name = "dept_scope", length = 20)
    @Builder.Default
    private String deptScope = "ALL";

    /** 组织层级：REGION, CITY, COUNTY, DEPT */
    @Column(name = "org_level", length = 20)
    private String orgLevel;

    /** 关联组织ID */
    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;

    @Transient
    @Builder.Default
    private Boolean hasChildren = false;
}
