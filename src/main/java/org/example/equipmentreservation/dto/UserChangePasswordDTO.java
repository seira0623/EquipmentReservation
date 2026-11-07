package org.example.equipmentreservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserChangePasswordDTO {
    @NotBlank(message = "原密码不能为空")
    private String password;
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度不能少于6位,不能超过64位")
    private String newPassword;
}
