package org.example.equipmentreservation.vo.equipment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EquipmentVO {
    private Long id;
    private String equipmentNumber;
    private String equipmentName;
    private Long categoryId;
    private String categoryName;
    private String lab;
    private String model;
    private String status;
    private String description;
    private LocalDateTime purchaseTime;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
