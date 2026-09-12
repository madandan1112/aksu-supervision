package cn.aksu.supervision.report.entity;

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
@Table(name = "compliance_report")
public class ComplianceReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 20)
    private String reportType;

    @Column(length = 500)
    private String fileUrl;

    @Column(length = 100)
    private String fileName;

    @Column(name = "expiry_date")
    private LocalDateTime expireDate;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "review_remark", length = 500)
    private String reviewComment;

    @Column(length = 50)
    private String reviewedBy;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewTime;

    @Column(length = 50)
    private String uploadedBy;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;

    @Transient
    private String enterpriseName;

    @Transient
    private String reportNo;
}
