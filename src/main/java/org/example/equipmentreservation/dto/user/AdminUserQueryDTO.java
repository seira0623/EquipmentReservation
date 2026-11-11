package org.example.equipmentreservation.dto.user;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdminUserQueryDTO {
    private Long id;
    private String username;
    private String studentNumber;
    private String phone;
    private Integer role;

    //分页参数
    @Min(value = 1, message = "当前页码不能小于1")
    private Long current = 1L;
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Long size = 10L;
}
