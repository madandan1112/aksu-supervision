package cn.aksu.supervision.system.entity;

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
@Table(name = "sys_job_title")
public class SysJobTitle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title_code", nullable = false, unique = true, length = 50)
    private String titleCode;

    @Column(name = "title_name", nullable = false, length = 100)
    private String titleName;

    /** 类别：TECHNICAL技术/MANAGEMENT管理/ADMIN行政 */
    @Column(length = 20)
    private String category;

    /** 是否领导职务：1是,0否 */
    @Column(name = "is_leadership", nullable = false)
    @Builder.Default
    private Integer isLeadership = 0;

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
}