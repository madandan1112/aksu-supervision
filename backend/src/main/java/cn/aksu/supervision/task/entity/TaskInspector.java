package cn.aksu.supervision.task.entity;

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
@Table(name = "task_inspector")
public class TaskInspector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long taskId;

    @Column(name = "user_id", nullable = false)
    private Long inspectorId;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "ASSIGNED";

    @Column
    private LocalDateTime claimTime;

    @Column
    private LocalDateTime completeTime;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;
}
