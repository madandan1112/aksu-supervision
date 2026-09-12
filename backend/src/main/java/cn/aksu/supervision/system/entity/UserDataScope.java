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
@Table(name = "user_data_scope",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "org_id"}, name = "uk_user_org"))
public class UserDataScope {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "org_id", nullable = false)
    private Long orgId;

    /** 组织类型：REGION, CITY, COUNTY, DEPT */
    @Column(name = "org_type", length = 20, nullable = false)
    private String orgType;

    /** 数据范围：ALL, REGION, CITY, COUNTY, DEPT, SELF */
    @Column(name = "data_scope", length = 20)
    @Builder.Default
    private String dataScope = "SELF";

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
