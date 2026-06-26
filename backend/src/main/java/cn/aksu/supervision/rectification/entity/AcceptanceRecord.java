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
@Table(name = "acceptance_record")
public class AcceptanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long noticeId;

    @Column(nullable = false)
    private Long inspectorId;

    @Column(nullable = false, length = 20)
    private String conclusion;

    @Column(columnDefinition = "TEXT")
    private String opinion;

    @Column(length = 2000)
    private String evidenceImages;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;
}
