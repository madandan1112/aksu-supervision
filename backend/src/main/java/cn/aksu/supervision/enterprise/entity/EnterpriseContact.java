package cn.aksu.supervision.enterprise.entity;

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
@Table(name = "enterprise_contact")
public class EnterpriseContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long enterpriseId;

    @Column(nullable = false, length = 50)
    private String contactName;

    @Column(nullable = false, length = 20)
    private String contactPhone;

    @Column(length = 50)
    private String position;

    @Column(length = 50)
    private String email;

    @Column(nullable = false)
    @Builder.Default
    private Integer isPrimary = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updateTime;
}
