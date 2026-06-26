package cn.aksu.supervision.enterprise.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseDTO {

    private Long id;
    private String creditCode;
    private String name;
    private String legalPerson;
    private String phone;
    private String email;
    private String industry;
    private String area;
    private String address;
    private String businessScope;
    private String licenseUrl;
    private Integer status;
    private Long userId;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
