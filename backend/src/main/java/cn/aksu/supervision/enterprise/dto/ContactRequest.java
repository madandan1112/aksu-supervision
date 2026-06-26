package cn.aksu.supervision.enterprise.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactRequest {

    @NotBlank(message = "联系人姓名不能为空")
    private String contactName;

    @NotBlank(message = "联系人电话不能为空")
    private String contactPhone;

    private String position;

    private String email;

    private Integer isPrimary;
}
