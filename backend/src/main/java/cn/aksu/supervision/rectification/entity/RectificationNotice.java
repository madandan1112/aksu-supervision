package cn.aksu.supervision.rectification.entity;

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
@Table(name = "rectification_notice")
public class RectificationNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inspectionRecordId;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(nullable = false)
    private Long inspectorId;

    @Column(nullable = false, length = 200)
    private String noticeNo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String issues;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String requirements;

    @Column(nullable = false)
    private LocalDateTime deadline;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ISSUED";

    @Column(length = 500)
    private String attachmentUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
