package org.example.equipmentreservation.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class EquipmentCategoryModifyDTO {
    @NotBlank(message = "类别不能为空")
    @Size(max = 50, message = "分类名称不能超过50个字符")
    private String category;
    @Size(max = 255, message = "分类描述不能超过255个字符")
    private String description;
}
