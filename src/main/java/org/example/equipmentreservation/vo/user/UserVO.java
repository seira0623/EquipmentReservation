package org.example.equipmentreservation.vo.user;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserVO {
    private Long id;
    private String username;
    private String studentNumber;
    private String phone;
    private Integer role;
    private String roleName;
    private LocalDateTime createTime;
}
