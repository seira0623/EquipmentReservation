package org.example.equipmentreservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.equipmentreservation.entity.Equipment;

@Mapper
public interface EquipmentMapper extends BaseMapper<Equipment> {
}
