package cn.aksu.supervision.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_position")
public class SysPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "position_code", nullable = false, unique = true, length = 50)
    private String positionCode;

    @Column(name = "position_name", nullable = false, length = 100)
    private String positionName;

    /** 类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政 */
    @Column(length = 20)
    private String category;

    @Column(name = "salary_range_min", precision = 10, scale = 2)
    private BigDecimal salaryRangeMin;

    @Column(name = "salary_range_max", precision = 10, scale = 2)
    private BigDecimal salaryRangeMax;

    /** 所属组织ID */
    @Column(name = "org_id")
    private Long orgId;

    @Column(nullable = false)
    @Builder.Default
    private Integer level = 1;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private String orgName;
}