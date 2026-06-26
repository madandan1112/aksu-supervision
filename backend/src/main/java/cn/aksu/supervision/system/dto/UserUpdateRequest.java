package cn.aksu.supervision.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserUpdateRequest {

    private String realName;
    private String phone;
    private String email;
    private String userType;
    private Integer status;
    private String avatar;
}
