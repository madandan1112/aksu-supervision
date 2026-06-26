package cn.aksu.supervision.alert.entity;

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
@Table(name = "alert")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String alertType;

    @Column(name = "alert_level", nullable = false, length = 20)
    private String level;

    @Column
    private Long enterpriseId;

    @Column(length = 200)
    private String enterpriseName;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "handler_id", length = 50)
    private String handledBy;

    @Column(name = "handle_remark", columnDefinition = "TEXT")
    private String handleResult;

    @Column(name = "handled_at")
    private LocalDateTime handleTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
