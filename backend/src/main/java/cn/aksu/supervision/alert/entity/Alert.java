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

    @Column(nullable = false, length = 30)
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

    /** 预警来源: SCHEDULED(定时扫描)/EVENT(事件触发) */
    @Column(length = 20)
    @Builder.Default
    private String source = "SCHEDULED";

    /** 派单处理人ID */
    @Column(name = "assigned_user_id")
    private Long assignedUserId;

    /** 派单处理人姓名 */
    @Column(name = "assigned_user_name", length = 50)
    private String assignedUserName;

    /** 处理人 */
    @Column(name = "handler_id", length = 50)
    private String handledBy;

    /** 处理结果 */
    @Column(name = "handle_remark", columnDefinition = "TEXT")
    private String handleResult;

    /** 处理时间 */
    @Column(name = "handled_at")
    private LocalDateTime handleTime;

    /** 督办升级时间（超过此时间未处理则升级） */
    @Column(name = "escalate_at")
    private LocalDateTime escalateAt;

    /** 督办升级次数 */
    @Column(name = "escalate_count")
    @Builder.Default
    private Integer escalateCount = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
