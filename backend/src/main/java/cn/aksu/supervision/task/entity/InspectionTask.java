package cn.aksu.supervision.task.entity;

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
@Table(name = "inspection_task")
public class InspectionTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_no", nullable = false, length = 30, unique = true)
    private String taskNo;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String taskType;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "PENDING";

    @Column(name = "start_date")
    private LocalDateTime plannedStartTime;

    @Column(name = "end_date")
    private LocalDateTime plannedEndTime;

    @Column
    private LocalDateTime actualStartTime;

    @Column
    private LocalDateTime actualEndTime;

    @Column(length = 50)
    private String createdBy;

    @Column(columnDefinition = "TEXT")
    private String checkItems;

    @Column(name = "terminated_reason", columnDefinition = "TEXT")
    private String terminateReason;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;

    @PrePersist
    protected void onCreate() {
        if (taskNo == null || taskNo.isEmpty()) {
            taskNo = "TASK" + System.currentTimeMillis();
        }
    }
}
