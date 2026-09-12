package cn.aksu.supervision.inspection.entity;

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
@Table(name = "inspection_record")
public class InspectionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(nullable = false)
    private Long inspectorId;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "DRAFT";

    @Column(columnDefinition = "TEXT")
    private String formData;

    @Column(name = "check_type", length = 500)
    private String checkType;

    @Column(name = "attachment_urls", length = 2000)
    private String attachmentUrls;

    @Column(columnDefinition = "TEXT")
    private String issues;

    @Column(length = 2000)
    private String evidenceImages;

    @Column(length = 2000)
    private String evidenceVideos;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "check_date")
    private LocalDateTime inspectionTime;

    @Column(length = 50)
    private String inspectorName;

    @Column(length = 200)
    private String enterpriseName;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
