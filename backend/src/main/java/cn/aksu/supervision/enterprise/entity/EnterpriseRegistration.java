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
@Table(name = "enterprise_registration")
public class EnterpriseRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Builder.Default
    private Integer step = 1;

    @Column(length = 50)
    private String creditCode;

    @Column(length = 200)
    private String enterpriseName;

    @Column(length = 200)
    private String legalPerson;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(length = 100)
    private String industry;

    @Column(name = "industry_type_code", length = 50)
    private String industryTypeCode;

    @Column(length = 100)
    private String area;

    @Column(length = 500)
    private String licenseUrl;

    @Column(columnDefinition = "JSON")
    private String qualificationUrls;

    @Column(name = "storefront_photo", length = 500)
    private String storefrontPhoto;

    @Column(name = "interior_photo", length = 500)
    private String interiorPhoto;

    /** DRAFT/SUBMITTED/APPROVED/REJECTED */
    @Column(length = 20)
    @Builder.Default
    private String status = "DRAFT";

    @Column(name = "review_comment", length = 500)
    private String reviewComment;

    @Column(name = "reviewed_by")
    private Long reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
