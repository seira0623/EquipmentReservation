package org.example.equipmentreservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.equipmentreservation.entity.Equipment;

import java.time.LocalDateTime;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {

    //悲观锁用id查询
    @Select("""
            select *
            from equipment
            where id = #{id}
            for update
            """)
    Equipment selectByIdForUpdate(@Param("id") Long id);
}
