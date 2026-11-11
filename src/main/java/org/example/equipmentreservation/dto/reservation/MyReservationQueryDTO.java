package org.example.equipmentreservation.dto.reservation;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MyReservationQueryDTO {
    private Long equipmentId;
    private String status;
    private LocalDateTime startTime;
    private LocalDateTime plannedEndTime;

    @Min(value = 1, message = "页码必须大于0")
    private Long current = 1L;

    @Min(value = 1, message = "每页数量必须大于0")
    @Max(value = 100, message = "每页最多查询100条")
    private Long size = 10L;
}
