package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.dto.category.EquipmentCategoryAddDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryModifyDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryQueryDTO;
import org.example.equipmentreservation.entity.Equipment;
import org.example.equipmentreservation.entity.EquipmentCategory;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.EquipmentCategoryMapper;
import org.example.equipmentreservation.mapper.EquipmentMapper;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.EquipmentCategoryService;
import org.example.equipmentreservation.vo.category.CategoryVO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentCategoryServiceImpl implements EquipmentCategoryService {

    private final EquipmentCategoryMapper equipmentCategoryMapper;
    private final UserMapper userMapper;
    private final EquipmentMapper equipmentMapper;

    @Override
    public CategoryVO addCategory(EquipmentCategoryAddDTO equipmentCategoryAddDTO) {
        String category = equipmentCategoryAddDTO.getCategory();
        String description = equipmentCategoryAddDTO.getDescription();

        SysUser isAdmin = userMapper.selectById(UserContext.getCurrentUserId());
        if (isAdmin == null || !Integer.valueOf(1).equals(isAdmin.getRole())) {
            throw new BussinessException("无权限增加类别");
        }

        LambdaQueryWrapper<EquipmentCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EquipmentCategory::getCategory, category);
        if (equipmentCategoryMapper.selectCount(lambdaQueryWrapper) > 0) {
            throw new BussinessException("类别名字已存在");
        }

        EquipmentCategory equipmentCategory = new EquipmentCategory();
        equipmentCategory.setCategory(category);
        equipmentCategory.setDescription(description);
        equipmentCategoryMapper.insert(equipmentCategory);

        return getCategoryVO(equipmentCategory);
    }

    @Nonnull
    private static CategoryVO getCategoryVO(EquipmentCategory equipmentCategory) {
        CategoryVO categoryVO = new CategoryVO();
        categoryVO.setCategory(equipmentCategory.getCategory());
        categoryVO.setDescription(equipmentCategory.getDescription());
        categoryVO.setId(equipmentCategory.getId());
        categoryVO.setCreateTime(equipmentCategory.getCreateTime());
        categoryVO.setUpdateTime(equipmentCategory.getUpdateTime());

        return categoryVO;
    }

    @Override
    public CategoryVO modifyCategory(Long id, EquipmentCategoryModifyDTO equipmentCategoryModifyDTO) {
        String category = equipmentCategoryModifyDTO.getCategory();
        String description = equipmentCategoryModifyDTO.getDescription();

        SysUser isAdmin = userMapper.selectById(UserContext.getCurrentUserId());
        if (isAdmin == null || !Integer.valueOf(1).equals(isAdmin.getRole())) {
            throw new BussinessException("无权限修改类别");
        }

        EquipmentCategory equipmentCategory = equipmentCategoryMapper.selectById(id);
        if (equipmentCategory == null) {
            throw new BussinessException("类别不存在");
        }

        LambdaQueryWrapper<EquipmentCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(EquipmentCategory::getCategory, category)
                .ne(EquipmentCategory::getId, id);
        if (equipmentCategoryMapper.selectCount(lambdaQueryWrapper) > 0) {
            throw new BussinessException("类别名字已存在");
        }

        equipmentCategory.setCategory(category);
        equipmentCategory.setDescription(description);
        equipmentCategoryMapper.updateById(equipmentCategory);

        return getCategoryVO(equipmentCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        EquipmentCategory category = equipmentCategoryMapper.selectById(id);

        if (category == null) {
            throw new BussinessException("设备分类不存在");
        }

        SysUser isAdmin = userMapper.selectById(UserContext.getCurrentUserId());
        if (isAdmin == null || !Integer.valueOf(1).equals(isAdmin.getRole())) {
            throw new BussinessException("无权限删除类别");
        }

        LambdaQueryWrapper<Equipment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Equipment::getCategoryId, id);
        Long equipmentCount = equipmentMapper.selectCount(lambdaQueryWrapper);

        if (equipmentCount > 0) {
            throw new BussinessException("类别下有设备，不能删除");
        }
        equipmentCategoryMapper.deleteById(id);
    }

    @Override
    public CategoryVO getCategoryById(Long id) {
        EquipmentCategory equipmentCategory = equipmentCategoryMapper.selectById(id);
        if (equipmentCategory == null) {
            throw new BussinessException("类别不存在");
        }

        return getCategoryVO(equipmentCategory);
    }

    @Override
    public List<CategoryVO> getCategoryList(EquipmentCategoryQueryDTO equipmentCategoryQueryDTO) {
        Long id = equipmentCategoryQueryDTO.getId();
        String category = equipmentCategoryQueryDTO.getCategory();

        LambdaQueryWrapper<EquipmentCategory> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, EquipmentCategory::getId, id)
                .like(category != null && !category.isBlank(), EquipmentCategory::getCategory, category);

        List<EquipmentCategory> equipmentCategories = equipmentCategoryMapper.selectList(lambdaQueryWrapper);
        return equipmentCategories.stream().map(equipmentCategory -> getCategoryVO(equipmentCategory)).toList();
    }
}
