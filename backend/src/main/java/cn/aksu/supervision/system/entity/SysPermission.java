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
@Table(name = "sys_permission")
public class SysPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String permissionCode;

    @Column(nullable = false, length = 100)
    private String permissionName;

    @Column(name = "resource_type", length = 20)
    private String permissionType;

    @Column(length = 50)
    private String module;

    @Column(length = 200)
    private String url;

    @Column(length = 20)
    private String method;

    @Column(name = "parent_id", length = 50)
    private String parentId;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
