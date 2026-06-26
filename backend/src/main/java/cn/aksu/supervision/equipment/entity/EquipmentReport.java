package cn.aksu.supervision.equipment.entity;

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
@Table(name = "equipment_report")
public class EquipmentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long equipmentId;

    @Column(nullable = false, length = 20)
    private String reportType;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 2000)
    private String images;

    @Column(length = 50)
    private String reporter;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;
}
