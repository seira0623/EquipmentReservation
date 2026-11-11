package org.example.equipmentreservation.dto.reservation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCreateDTO {
    @NotNull(message = "设备ID不能为空")
    private Long equipmentId;
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;
    @NotNull(message = "计划结束时间不能为空")
    private LocalDateTime plannedEndTime;
    @NotBlank(message = "用途不能为空")
    @Size(min = 1, max = 100, message = "用途长度在1到100个字符之间")
    private String purpose;
}
