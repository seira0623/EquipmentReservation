package org.example.equipmentreservation.vo.Reservation;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationSlotVO {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
}
