package org.example.equipmentreservation.vo.Reservation;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ReservationCalendarVO {
    private Long equipmentId;
    private LocalDateTime queryStartTime;
    private LocalDateTime queryEndTime;
    private List<ReservationSlotVO> slots;
}
