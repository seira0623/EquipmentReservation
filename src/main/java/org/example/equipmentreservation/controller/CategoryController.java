package org.example.equipmentreservation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.common.Result;
import org.example.equipmentreservation.dto.category.EquipmentCategoryAddDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryModifyDTO;
import org.example.equipmentreservation.dto.category.EquipmentCategoryQueryDTO;
import org.example.equipmentreservation.service.EquipmentCategoryService;
import org.example.equipmentreservation.vo.category.CategoryVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final EquipmentCategoryService equipmentCategoryService;

    @PostMapping
    public Result<CategoryVO> addCategory(@RequestBody @Valid EquipmentCategoryAddDTO equipmentCategoryAddDTO) {
        CategoryVO categoryVO = equipmentCategoryService.addCategory(equipmentCategoryAddDTO);
        return Result.success(categoryVO);
    }

    @PutMapping("/{id}")
    public Result<CategoryVO> modifyCategory(@PathVariable Long id,@RequestBody @Valid EquipmentCategoryModifyDTO equipmentCategoryModifyDTO) {
        CategoryVO categoryVO = equipmentCategoryService.modifyCategory(id,equipmentCategoryModifyDTO);
        return Result.success(categoryVO);
    }

    @DeleteMapping("/{id}")
    public Result deleteCategory(@PathVariable Long id) {
        equipmentCategoryService.deleteCategory(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public Result<CategoryVO> getCategoryById(@PathVariable Long id) {
        CategoryVO categoryVO = equipmentCategoryService.getCategoryById(id);
        return Result.success(categoryVO);
    }

    @GetMapping("/list")
    public Result<List<CategoryVO>> getCategoryList(@Valid @ModelAttribute EquipmentCategoryQueryDTO equipmentCategoryQueryDTO) {
        List<CategoryVO> categoryVOList = equipmentCategoryService.getCategoryList(equipmentCategoryQueryDTO);
        return Result.success(categoryVOList);
    }
}
