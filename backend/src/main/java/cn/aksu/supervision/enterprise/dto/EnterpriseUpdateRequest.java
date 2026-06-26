package cn.aksu.supervision.enterprise.dto;

import lombok.Data;

@Data
public class EnterpriseUpdateRequest {

    private String name;
    private String legalPerson;
    private String phone;
    private String email;
    private String industry;
    private String area;
    private String address;
    private String businessScope;
    private String licenseUrl;
}
