package org.example.equipmentreservation.dto.equipment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentAddDTO {
    @NotBlank(message = "设备编号不能为空")
    @Size(max = 50, message = "设备编号不能超过50个字符")
    private String equipmentNumber;
    @NotBlank(message = "设备名称不能为空")
    private String equipmentName;
    @NotNull(message = "设备类别不能为空")
    private Long categoryId;
    private String lab;
    private String model;
    private String description;
    private LocalDateTime purchaseTime;
}
