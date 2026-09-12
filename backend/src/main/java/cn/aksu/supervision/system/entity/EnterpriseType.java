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
@Table(name = "enterprise_type")
public class EnterpriseType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type_code", nullable = false, unique = true, length = 50)
    private String typeCode;

    @Column(name = "type_name", nullable = false, length = 100)
    private String typeName;

    /** 大类：FOOD食品/DRUG药品/MEDICAL医疗器械/SPECIAL_EQUIP特种设备/HOTEL酒店/FACTORY工厂/HAZARDOUS危化品/COSMETICS化妆品/TOBACCO烟草/TRADE商贸/SERVICE服务/OTHER其他 */
    @Column(length = 50)
    private String category;

    /** 所需证照JSON数组 */
    @Column(name = "required_license", columnDefinition = "TEXT")
    private String requiredLicense;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    @Builder.Default
    private Integer status = 1;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0;

    @CreationTimestamp
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;

    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;
}