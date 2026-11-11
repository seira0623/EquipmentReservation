package org.example.equipmentreservation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.common.Result;
import org.example.equipmentreservation.dto.equipment.EquipmentAddDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentModifyDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentQueryDTO;
import org.example.equipmentreservation.dto.equipment.EquipmentStatusDTO;
import org.example.equipmentreservation.service.EquipmentService;
import org.example.equipmentreservation.vo.equipment.EquipmentVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public Result<EquipmentVO> addEquipment(@RequestBody @Valid EquipmentAddDTO equipmentAddDTO) {
        EquipmentVO equipmentVO = equipmentService.addEquipment(equipmentAddDTO);
        return Result.success(equipmentVO);
    }

    @PatchMapping("/{id}")
    public Result<EquipmentVO> modifyEquipment(@PathVariable Long id, @RequestBody @Valid EquipmentModifyDTO equipmentModifyDTO) {
        EquipmentVO equipmentVO = equipmentService.modifyEquipment(id, equipmentModifyDTO);
        return Result.success(equipmentVO);
    }

    @PutMapping("{id}/disable")
    public Result<Void> disableEquipment(@PathVariable Long id) {
        equipmentService.disableEquipment(id);
        return Result.success();
    }

    @PutMapping("{id}/status")
    public Result<Void> updateEquipmentStatus(@PathVariable Long id, @RequestBody @Valid EquipmentStatusDTO equipmentStatusDTO) {
        equipmentService.updateEquipmentStatus(id, equipmentStatusDTO);
        return Result.success();
    }

    @GetMapping("{id}")
    public Result<EquipmentVO> getEquipment(@PathVariable Long id) {
        EquipmentVO equipmentVO = equipmentService.getEquipmentById(id);
        return Result.success(equipmentVO);
    }

    @GetMapping
    public Result<IPage<EquipmentVO>> listEquipment(@Valid @ModelAttribute EquipmentQueryDTO equipmentQueryDTO) {
        IPage<EquipmentVO> equipmentVOIPage = equipmentService.pageEquipment(equipmentQueryDTO);
        return Result.success(equipmentVOIPage);
    }

}
