package cn.aksu.supervision.appeal.entity;

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
@Table(name = "appeal")
public class Appeal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(nullable = false, length = 20)
    private String appealType;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String relatedFields;

    @Column(length = 2000)
    private String attachments;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "assigned_user_id", length = 50)
    private String assignedTo;

    @Column(name = "result_content", columnDefinition = "TEXT")
    private String handleResult;

    @Column(columnDefinition = "TEXT")
    private String handleAttachment;

    @Column
    private LocalDateTime handleTime;

    @Column(name = "satisfaction_score")
    private Integer satisfaction;

    @Column(name = "satisfaction_comment", length = 500)
    private String evaluateComment;

    @Column
    private LocalDateTime evaluateTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
