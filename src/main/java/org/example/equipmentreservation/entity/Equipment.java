package org.example.equipmentreservation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("equipment")
public class Equipment {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String equipmentNumber;
    private String equipmentName;
    private Long categoryId;
    private String lab;
    private String model;
    private String status;
    private String description;
    private LocalDateTime purchaseTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
