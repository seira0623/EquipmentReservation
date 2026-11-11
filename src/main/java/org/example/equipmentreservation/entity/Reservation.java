package org.example.equipmentreservation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("reservation")
public class Reservation {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long equipmentId;
    private LocalDateTime startTime;
    private LocalDateTime borrowTime;
    private LocalDateTime plannedEndTime;
    private LocalDateTime actualReturnTime;
    private String status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    private String purpose;
    private Long reviewerId;
    private LocalDateTime reviewedTime;
    private String rejectReason;
    private String cancelReason;
}
