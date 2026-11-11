package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.dto.equipment.EquipmentAddDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentModifyDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentQueryDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentStatusDTO;
import org.example.equipmentreservation.entity.Equipment;
import org.example.equipmentreservation.entity.EquipmentCategory;
import org.example.equipmentreservation.entity.Reservation;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.EquipmentCategoryMapper;
import org.example.equipmentreservation.mapper.EquipmentMapper;
import org.example.equipmentreservation.mapper.ReservationMapper;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.EquipmentService;
import org.example.equipmentreservation.vo.equipment.EquipmentVO;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class EquipmentServiceImpl implements EquipmentService {

    private final UserMapper userMapper;
    private final EquipmentMapper equipmentMapper;
    private final EquipmentCategoryMapper equipmentCategoryMapper;
    private final ReservationMapper reservationMapper;




    @Override
    public EquipmentVO addEquipment(EquipmentAddDTO equipmentAddDTO) {
        checkAdmin();

        String equipmentNumber = equipmentAddDTO.getEquipmentNumber();
        String equipmentName = equipmentAddDTO.getEquipmentName();
        Long categoryId = equipmentAddDTO.getCategoryId();
        String lab = equipmentAddDTO.getLab();
        String model = equipmentAddDTO.getModel();
        String description = equipmentAddDTO.getDescription();
        LocalDateTime purchaseTime = equipmentAddDTO.getPurchaseTime();

        LambdaQueryWrapper<Equipment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Equipment::getEquipmentNumber, equipmentNumber);
        Equipment equipment = equipmentMapper.selectOne(lambdaQueryWrapper);
        if (equipment != null) {
            throw new BussinessException("设备编号已存在");
        }

        EquipmentCategory category = equipmentCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BussinessException("设备分类不存在");
        }

        Equipment newEquipment = new Equipment();
        newEquipment.setEquipmentNumber(equipmentNumber);
        newEquipment.setEquipmentName(equipmentName);
        newEquipment.setCategoryId(categoryId);
        newEquipment.setLab(lab);
        newEquipment.setModel(model);
        newEquipment.setStatus("AVAILABLE");
        newEquipment.setDescription(description);
        newEquipment.setPurchaseTime(purchaseTime);
        equipmentMapper.insert(newEquipment);

        return getEquipmentVO(newEquipment);
    }

    private void checkAdmin() {
        SysUser isAdmin = userMapper.selectById(UserContext.getCurrentUserId());
        if(isAdmin == null || !Integer.valueOf(1).equals(isAdmin.getRole())){
            throw new BussinessException("用户不存在或无权限");
        }
    }

    @Override
    public EquipmentVO modifyEquipment(Long id, EquipmentModifyDTO equipmentModifyDTO) {
        checkAdmin();

        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }

        String equipmentName = equipmentModifyDTO.getEquipmentName();
        Long categoryId = equipmentModifyDTO.getCategoryId();
        String lab = equipmentModifyDTO.getLab();
        String model = equipmentModifyDTO.getModel();
        String description = equipmentModifyDTO.getDescription();
        LocalDateTime purchaseTime = equipmentModifyDTO.getPurchaseTime();

        if(equipmentName != null){
            equipment.setEquipmentName(equipmentName);
        }
        if(categoryId != null){
            EquipmentCategory category = equipmentCategoryMapper.selectById(categoryId);
            if (category == null) {
                throw new BussinessException("设备分类不存在");
            }
            equipment.setCategoryId(categoryId);
        }
        if(lab != null){
            equipment.setLab(lab);
        }
        if(model != null){
            equipment.setModel(model);
        }
        if(description != null){
            equipment.setDescription(description);
        }
        if(purchaseTime != null){
            equipment.setPurchaseTime(purchaseTime);
        }
        equipmentMapper.updateById(equipment);

        return getEquipmentVO(equipment);
    }

    public enum EquipmentStatus {

        AVAILABLE,
        MAINTENANCE,
        DISABLED
    }

    public enum ReservationStatus{
        PENDING,
        APPROVED,
        REJECTED,
        CANCELLED,
        BORROWED,
        COMPLETED
    }

    @Override
    public void disableEquipment(Long id) {
        checkAdmin();
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }
        //查询该仪器是否还有不是complete的预约记录
        LambdaQueryWrapper<Reservation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reservation::getEquipmentId, id)
                .in(Reservation::getStatus,
                ReservationStatus.PENDING.name(),
                ReservationStatus.APPROVED.name(),
                ReservationStatus.BORROWED.name());
        Long count = reservationMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BussinessException("设备有未完成的预约记录，无法禁用");
        }

        if(equipment.getStatus().equals(EquipmentStatus.DISABLED.name())){
            throw new BussinessException("设备已禁用，无法重复禁用");
        }
        equipment.setStatus(EquipmentStatus.DISABLED.name());
        equipmentMapper.updateById(equipment);
        log.info("管理员将设备{}设置为停用状态", id);
    }

    @Override
    public void updateEquipmentStatus(Long id, EquipmentStatusDTO equipmentStatusDTO) {
        checkAdmin();
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }

        String oldStatus = equipment.getStatus();

        EquipmentStatus targetStatus;
        try {
            targetStatus = EquipmentStatus.valueOf(
                    equipmentStatusDTO.getStatus()
            );
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new BussinessException("无效的设备状态");
        }

        if(equipmentStatusDTO.getStatus().equals(EquipmentStatus.DISABLED.name())){
            throw new BussinessException("无法在此接口禁用设备，请去专门接口");
        }
        LambdaQueryWrapper<Reservation> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Reservation::getEquipmentId, id)
                .in(Reservation::getStatus,
                        ReservationStatus.PENDING.name(),
                        ReservationStatus.APPROVED.name(),
                        ReservationStatus.BORROWED.name());
        Long count = reservationMapper.selectCount(lambdaQueryWrapper);
        if (count > 0) {
            throw new BussinessException("设备有未完成的预约记录，无法禁用");
        }
        if(equipment.getStatus().equals(EquipmentStatus.DISABLED.name())){
            throw new BussinessException("设备已禁用，无法更新状态");
        }
        equipment.setStatus(targetStatus.name());
        equipmentMapper.updateById(equipment);
        log.info("设备{}状态由{}修改为{}", id, oldStatus, targetStatus.name());
    }

    @Override
    public EquipmentVO getEquipmentById(Long id) {
        Equipment equipment = equipmentMapper.selectById(id);
        if (equipment == null) {
            throw new BussinessException("设备不存在");
        }
        return getEquipmentVO(equipment);
    }

    @Nonnull
    private EquipmentVO getEquipmentVO(Equipment equipment) {
        EquipmentVO equipmentVO = new EquipmentVO();
        equipmentVO.setId(equipment.getId());
        equipmentVO.setEquipmentNumber(equipment.getEquipmentNumber());
        equipmentVO.setEquipmentName(equipment.getEquipmentName());
        equipmentVO.setCategoryId(equipment.getCategoryId());

        EquipmentCategory category = equipmentCategoryMapper.selectById(equipment.getCategoryId());
        if(category == null){
            throw new BussinessException("设备分类不存在");
        }
        equipmentVO.setCategoryName(category.getCategory());

        equipmentVO.setLab(equipment.getLab());
        equipmentVO.setModel(equipment.getModel());
        equipmentVO.setDescription(equipment.getDescription());
        equipmentVO.setPurchaseTime(equipment.getPurchaseTime());
        equipmentVO.setStatus(equipment.getStatus());
        equipmentVO.setCreateTime(equipment.getCreateTime());
        equipmentVO.setUpdateTime(equipment.getUpdateTime());
        return equipmentVO;
    }

    @Override
    public IPage<EquipmentVO> pageEquipment(EquipmentQueryDTO equipmentQueryDTO) {
        Long id = equipmentQueryDTO.getId();
        String equipmentNumber = equipmentQueryDTO.getEquipmentNumber();
        String equipmentName = equipmentQueryDTO.getEquipmentName();
        Long categoryId = equipmentQueryDTO.getCategoryId();
        String lab = equipmentQueryDTO.getLab();
        String model = equipmentQueryDTO.getModel();
        String status = equipmentQueryDTO.getStatus();
        Long current = equipmentQueryDTO.getCurrent();
        Long size = equipmentQueryDTO.getSize();

        LambdaQueryWrapper<Equipment> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(id != null, Equipment::getId, id)
                .eq(equipmentNumber != null && !equipmentNumber.isBlank(), Equipment::getEquipmentNumber, equipmentNumber)
                .like(equipmentName != null && !equipmentName.isBlank(), Equipment::getEquipmentName, equipmentName)
                .eq(categoryId != null, Equipment::getCategoryId, categoryId)
                .like(lab != null && !lab.isBlank(), Equipment::getLab, lab)
                .like(model != null && !model.isBlank(), Equipment::getModel, model)
                .eq(status != null && !status.isBlank(), Equipment::getStatus, status)
                .orderByDesc(Equipment::getCreateTime)
                .orderByDesc(Equipment::getId);

        IPage<Equipment> page = new Page<>(current, size);
        IPage<Equipment> equipmentPage = equipmentMapper.selectPage(page, lambdaQueryWrapper);

        // 获取所有设备分类ID
        List<Long> categoryIds = equipmentPage.getRecords().stream()
                .map(Equipment::getCategoryId)
                .distinct()
                .toList();
        List<EquipmentCategory> categories = equipmentCategoryMapper.selectBatchIds(categoryIds);
        Map<Long,String> categoryMap = categories.stream()
                .collect(Collectors.toMap(EquipmentCategory::getId, EquipmentCategory::getCategory));
        return equipmentPage.convert(equipment -> {
            EquipmentVO equipmentVO = new EquipmentVO();
            equipmentVO.setId(equipment.getId());
            equipmentVO.setEquipmentNumber(equipment.getEquipmentNumber());
            equipmentVO.setEquipmentName(equipment.getEquipmentName());
            equipmentVO.setCategoryId(equipment.getCategoryId());

            if(categoryMap.get(equipment.getCategoryId()) == null){
                throw new BussinessException("设备分类不存在");
            }
            equipmentVO.setCategoryName(categoryMap.get(equipment.getCategoryId()));

            equipmentVO.setLab(equipment.getLab());
            equipmentVO.setModel(equipment.getModel());
            equipmentVO.setDescription(equipment.getDescription());
            equipmentVO.setPurchaseTime(equipment.getPurchaseTime());
            equipmentVO.setStatus(equipment.getStatus());
            equipmentVO.setCreateTime(equipment.getCreateTime());
            equipmentVO.setUpdateTime(equipment.getUpdateTime());
            return equipmentVO;
        });
    }
}
