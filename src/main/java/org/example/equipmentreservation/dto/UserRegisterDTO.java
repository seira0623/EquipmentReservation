package org.example.equipmentreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegisterDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64,message = "密码长度不能少于6位,不能超过64位")
    private String password;
    @NotBlank(message = "学号不能为空")
    private String studentNumber;
    private String phone;
}
