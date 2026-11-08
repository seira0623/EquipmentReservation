package org.example.equipmentreservation.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;
    @ToString.Exclude
    private String password;

    private String studentNumber;
    private String phone;
    private Integer role; // 0: user, 1: admin

    private Integer tokenVersion;
    private Integer accountStatus; // 0: 正常 1: 禁用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
