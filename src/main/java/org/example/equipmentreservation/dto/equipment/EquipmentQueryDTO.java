package org.example.equipmentreservation.dto.equipment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentQueryDTO {
    private Long id;
    private String equipmentNumber;
    private String equipmentName;
    private Long categoryId;
    private String lab;
    private String model;
    private String status;


    @Min(value = 1, message = "当前页码不能小于1")
    private Long current = 1L;
    @Min(value = 1, message = "每页大小不能小于1")
    @Max(value = 100, message = "每页大小不能大于100")
    private Long size = 10L;
}
