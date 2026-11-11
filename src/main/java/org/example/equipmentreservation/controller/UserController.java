package org.example.equipmentreservation.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.common.Result;
import org.example.equipmentreservation.dto.user.*;
import org.example.equipmentreservation.service.UserService;
import org.example.equipmentreservation.vo.user.LoginVO;
import org.example.equipmentreservation.vo.user.UserVO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<UserVO> userRegister(@Valid @RequestBody UserRegisterDTO userRegisterDTO) {
        UserVO vo = userService.userRegister(userRegisterDTO);
        return Result.success(vo);
    }

    @PostMapping("/login")
    public Result<LoginVO> userLogin(@Valid @RequestBody UserLoginDTO userLoginDTO) {
        LoginVO vo = userService.userLogin(userLoginDTO);
        return Result.success(vo);
    }

    @GetMapping("/me")
    public Result<UserVO> currentUserInfoQuery() {
        UserVO vo = userService.currentUserInfoQuery();
        return Result.success(vo);
    }

    @GetMapping("/admin/users")
    public Result<IPage<UserVO>> userInfoQuery(@Valid @ModelAttribute AdminUserQueryDTO adminUserQueryDTO) {
        IPage<UserVO> page = userService.userInfoQuery(adminUserQueryDTO);
        return Result.success(page);
    }

    @PutMapping("/me")
    public Result<Void> userInfoUpdate(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        userService.userInfoUpdate(userUpdateDTO);
        return Result.success();
    }

    @PutMapping("/me/changePassword")
    public Result<Void> changePassword(@Valid @RequestBody UserChangePasswordDTO userChangePasswordDTO) {
        userService.changePassword(userChangePasswordDTO);
        return Result.success();
    }
}
