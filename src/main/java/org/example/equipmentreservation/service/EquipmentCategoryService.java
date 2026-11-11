package org.example.equipmentreservation.service;


import org.example.equipmentreservation.dto.category.EquipmentCategoryAddDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryModifyDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryQueryDTO;
import org.example.equipmentreservation.vo.category.CategoryVO;

import java.util.List;

public interface EquipmentCategoryService {
    /**
     * 添加设备类别
     * @param equipmentCategoryAddDTO
     * @return 设备类别对象
     */
    CategoryVO addCategory(EquipmentCategoryAddDTO equipmentCategoryAddDTO);

    /**
     * 修改设备类别
      * @param id, equipmentCategoryModifyDTO
     * @return 设备类别对象
     */
    CategoryVO modifyCategory(Long id, EquipmentCategoryModifyDTO equipmentCategoryModifyDTO);

    /**
     * 删除设备类别
     * @param id
     * @return
     */
    void deleteCategory(Long id);

    /**
     * 获取设备类别
      * @param id
     * @return 设备 类别对象
     */
    CategoryVO getCategoryById(Long id);

    /**
     * 获取设备类别列表
     * @param equipmentCategoryQueryDTO
     * @return 设备类别对象列表
     */
    List<CategoryVO> getCategoryList(EquipmentCategoryQueryDTO equipmentCategoryQueryDTO);
}
