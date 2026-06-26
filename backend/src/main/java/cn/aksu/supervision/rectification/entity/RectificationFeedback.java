package cn.aksu.supervision.rectification.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rectification_feedback")
public class RectificationFeedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long noticeId;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(name = "measures", columnDefinition = "TEXT")
    private String rectifyMeasures;

    @Column(name = "after_images", length = 2000)
    private String evidenceImages;

    @Column(name = "after_videos", length = 2000)
    private String evidenceVideos;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "SUBMITTED";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;
}
