package org.example.equipmentreservation.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.example.equipmentreservation.dto.*;
import org.example.equipmentreservation.vo.LoginVO;
import org.example.equipmentreservation.vo.UserVO;

public interface UserService {
    /**
     * 用户注册方法
     * @param userRegisterDTO
     * @return 用户信息
     */
    UserVO userRegister(UserRegisterDTO userRegisterDTO);

    /**
     * 用户登录方法
     * @param userLoginDTO
     * @return 登录结果
     */
    LoginVO userLogin(UserLoginDTO userLoginDTO);

    /**
     * 当前用户信息查询方法
     * @param
     * @return 用户信息
     */
    UserVO currentUserInfoQuery();

    /**
     * 管理员查询用户信息方法
     * @param userQueryDTO
     * @return 分页显示用户信息
     */
    IPage<UserVO> userInfoQuery(AdminUserQueryDTO userQueryDTO);

    /**
     * 用户更新方法
     * @param userUpdateDTO
     * @return
     */
    void userInfoUpdate(UserUpdateDTO userUpdateDTO);

    /**
     * 用户密码修改方法
     * @param userChangePasswordDTO
     * @return 修改结果
     */
    void changePassword(UserChangePasswordDTO userChangePasswordDTO);
}
