package cn.aksu.supervision.system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "sys_user_position",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "org_id"}))
public class OrgUserPosition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    @Column(name = "position_id")
    private Long positionId;

    @Column(name = "job_title_id")
    private Long jobTitleId;

    /** 是否主职：1主职,0兼职 */
    @Column(name = "is_primary", nullable = false)
    @Builder.Default
    private Integer isPrimary = 1;

    /** 任职开始日期 */
    @Column(name = "start_date")
    private LocalDate startDate;

    /** 任职结束日期 */
    @Column(name = "end_date")
    private LocalDate endDate;

    /** 状态：1在职,0离职 */
    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    @Transient
    private String username;

    @Transient
    private String realName;

    @Transient
    private String orgName;

    @Transient
    private String positionName;

    @Transient
    private String jobTitleName;
}