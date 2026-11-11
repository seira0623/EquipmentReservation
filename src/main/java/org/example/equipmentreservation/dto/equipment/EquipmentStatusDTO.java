package org.example.equipmentreservation.dto.equipment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EquipmentStatusDTO {
    @NotBlank(message = "设备状态不能为空")
    private String status;
}
