package org.example.equipmentreservation.vo.category;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CategoryVO {
    private Long id;
    private String category;
    private String description;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
