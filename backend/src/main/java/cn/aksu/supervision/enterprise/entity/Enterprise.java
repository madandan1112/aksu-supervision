package cn.aksu.supervision.enterprise.entity;

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
@Table(name = "enterprise")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String creditCode;

    @Column(name = "enterprise_name", nullable = false, length = 200)
    private String name;

    @Column(length = 200)
    private String legalPerson;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String industry;

    @Column(length = 100)
    private String area;

    @Column(length = 500)
    private String address;

    @Column(length = 50)
    private String businessScope;

    @Column(length = 500)
    private String licenseUrl;

    /** 资质证照URL列表(JSON) */
    @Column(columnDefinition = "JSON")
    private String qualificationUrls;

    /** 门头照URL */
    @Column(name = "storefront_photo", length = 500)
    private String storefrontPhoto;

    /** 店内照片URL */
    @Column(name = "interior_photo", length = 500)
    private String interiorPhoto;

    /** 行业分类编码 */
    @Column(name = "industry_type_code", length = 50)
    private String industryTypeCode;

    /** 注册审核状态: PENDING/APPROVED/REJECTED */
    @Column(name = "registration_status", length = 20)
    @Builder.Default
    private String registrationStatus = "PENDING";

    /** 审核意见 */
    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    /** 审核人ID */
    @Column(name = "reviewed_by")
    private Long reviewedBy;

    /** 审核时间 */
    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column
    private Long userId;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
