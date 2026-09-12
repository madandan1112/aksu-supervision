package cn.aksu.supervision.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "org_structure")
public class OrgStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "parent_id")
    private Long parentId;

    /**
     * 层级：1-地区, 2-县市, 3-乡镇街道, 4-单位, 5-科室
     */
    @Column(nullable = false)
    private Integer level;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(length = 500)
    private String description;

    /** 组织编码，如 ORG-0001 */
    @Column(name = "org_code", length = 50)
    private String orgCode;

    /** 路径，如 /1/2/5/，用于加速祖先查询 */
    @Column(name = "org_path", length = 500)
    private String orgPath;

    /** 组织类型：企业/机关/事业单位/社会团体 */
    @Column(name = "org_type", length = 50)
    private String orgType;

    /** 县级行政区划细分：CITY-县级市, COUNTY-县（仅level=2有效） */
    @Column(name = "region_sub_type", length = 20)
    private String regionSubType;

    /** 统一社会信用代码 */
    @Column(name = "unified_social_credit_code", length = 18)
    private String unifiedSocialCreditCode;

    /** 负责人姓名 */
    @Column(name = "leader_name", length = 100)
    private String leaderName;

    /** 负责人电话 */
    @Column(name = "leader_phone", length = 20)
    private String leaderPhone;

    /** 地址 */
    @Column(length = 500)
    private String address;

    /** 备注 */
    @Column(length = 1000)
    private String remark;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    @Builder.Default
    private List<OrgStructure> children = new ArrayList<>();
}
