package org.example.equipmentreservation.vo.Reservation;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservationVO {
    private Long id;
    private Long userId;
    private Long equipmentId;
    private LocalDateTime startTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime createTime;
    private String status;
    private String purpose;
}
