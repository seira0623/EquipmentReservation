package org.example.equipmentreservation.dto.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationCalendarQueryDTO {
    @NotNull(message = "查询的设备id不能为空")
    private Long equipmentId;
}
