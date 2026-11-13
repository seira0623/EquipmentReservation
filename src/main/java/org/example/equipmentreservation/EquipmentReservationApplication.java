package org.example.equipmentreservation;

import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.UserMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@MapperScan("org.example.equipmentreservation.mapper")
public class EquipmentReservationApplication {
    public static void main(String[] args) {
        SpringApplication.run(EquipmentReservationApplication.class, args);
    }
}


