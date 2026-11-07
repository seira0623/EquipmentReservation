package org.example.equipmentreservation.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.equipmentreservation.entity.SysUser;

@Mapper
public interface UserMapper extends BaseMapper<SysUser> {
}
