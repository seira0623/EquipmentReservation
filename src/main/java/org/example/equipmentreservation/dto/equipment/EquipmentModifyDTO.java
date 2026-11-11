package org.example.equipmentreservation.dto.equipment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentModifyDTO {
    private String equipmentName;
    private Long categoryId;
    private String lab;
    private String model;
    private String description;
    private LocalDateTime purchaseTime;
}
