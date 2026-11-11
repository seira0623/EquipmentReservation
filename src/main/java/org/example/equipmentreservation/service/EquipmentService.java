package org.example.equipmentreservation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.equipmentreservation.dto.equipment.EquipmentAddDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentModifyDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentQueryDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentStatusDTO;
import org.example.equipmentreservation.entity.Equipment;
import org.example.equipmentreservation.vo.equipment.EquipmentVO;

public interface EquipmentService {
    /**
     * 添加设备
     * @param equipmentAddDTO
     * @return
     */
    EquipmentVO addEquipment(EquipmentAddDTO equipmentAddDTO);

    /**
     * 修改设备
     * @param equipmentModifyDTO
     * @return
     */
    EquipmentVO modifyEquipment(Long id,EquipmentModifyDTO equipmentModifyDTO);

    /**
     * 删除设备
     * @param id
     * @return
     */
    void disableEquipment(Long id);

    /**
     * 更新设备状态
     * @param id
     * @param equipmentStatusDTO
     * @return
     */
    void updateEquipmentStatus(Long id,EquipmentStatusDTO equipmentStatusDTO);


    /**
     * 根据id获取设备
     * @param id
     * @return
     */
    EquipmentVO getEquipmentById(Long id);

    /**
     * 分页查询设备
     * @param equipmentQueryDTO
     * @return
     */
    IPage<EquipmentVO> pageEquipment(EquipmentQueryDTO equipmentQueryDTO);

}
