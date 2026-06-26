package cn.aksu.supervision.equipment.entity;

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
@Table(name = "equipment_ledger")
public class EquipmentLedger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(name = "device_name", nullable = false, length = 200)
    private String name;

    @Column(length = 50)
    private String model;

    @Column(length = 50)
    private String manufacturer;

    @Column(name = "use_location", length = 100)
    private String location;

    @Column
    private LocalDateTime installDate;

    @Column(name = "inspection_expiry")
    private LocalDateTime nextInspectionDate;

    @Column(length = 200)
    private String certificateNo;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String status = "NORMAL";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
