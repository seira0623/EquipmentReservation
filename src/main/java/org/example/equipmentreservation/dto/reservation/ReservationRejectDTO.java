package org.example.equipmentreservation.dto.reservation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationRejectDTO {
    @NotBlank(message = "拒绝原因不能为空")
    @Size(min = 1, max = 255, message = "拒绝原因长度在1到255个字符之间")
    private String rejectReason;
}
