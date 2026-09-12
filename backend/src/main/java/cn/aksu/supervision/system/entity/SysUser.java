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
@Table(name = "sys_user")
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50)
    private String realName;

    @Column(length = 20)
    private String phone;

    @Column(length = 50)
    private String email;

    @Column(nullable = false, length = 20)
    private String userType;

    /** 角色ID */
    @Column(name = "role_id")
    private Long roleId;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    /** 所属组织名称 */
    @Column(name = "org_name", length = 100)
    private String orgName;

    /** 岗位ID */
    @Column(name = "position_id")
    private Long positionId;

    /** 岗位名称 */
    @Column(name = "position_name", length = 100)
    private String positionName;

    /** 职务ID */
    @Column(name = "job_title_id")
    private Long jobTitleId;

    /** 职务名称 */
    @Column(name = "job_title_name", length = 100)
    private String jobTitleName;

    @Column(length = 500)
    private String avatar;

    /** 身份证 */
    @Column(name = "id_card", length = 18)
    private String idCard;

    /** 头像照片 */
    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    /** 执法证号 */
    @Column(name = "law_enforcement_no", length = 50)
    private String lawEnforcementNo;

    /** 个人资质 */
    @Column(name = "qualification_url", length = 500)
    private String qualificationUrl;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(length = 100)
    private String wxOpenid;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    private LocalDateTime updateTime;
}
