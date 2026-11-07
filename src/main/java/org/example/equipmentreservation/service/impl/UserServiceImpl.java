package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.dto.*;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.UserService;
import org.example.equipmentreservation.vo.LoginVO;
import org.example.equipmentreservation.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserVO userRegister(UserRegisterDTO userRegisterDTO) {
        //取出前端传来的字段
        String username = userRegisterDTO.getUsername();
        String password = userRegisterDTO.getPassword();
        String studentNumber = userRegisterDTO.getStudentNumber();
        String phone = userRegisterDTO.getPhone();

        //校验
        Long existUsername = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, username));
        if(existUsername > 0){
            throw new BussinessException("用户名已存在");
        }

        Long existStudentNumber = userMapper.selectCount(
                new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getStudentNumber, studentNumber));
        if(existStudentNumber > 0){
            throw new BussinessException("学号已存在");
        }

        //插入数据库
        SysUser newUser = new SysUser();
        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setStudentNumber(studentNumber);
        newUser.setRole(0);
        newUser.setPhone(phone);
        userMapper.insert(newUser);

        //返回结果
        UserVO userVO = new UserVO();
        userVO.setId(newUser.getId());
        userVO.setUsername(newUser.getUsername());
        userVO.setStudentNumber(newUser.getStudentNumber());
        userVO.setPhone(newUser.getPhone());
        userVO.setRole(newUser.getRole());
        userVO.setRoleName(
                newUser.getRole() == 1 ? "管理员" : "普通用户"
        );
        userVO.setCreateTime(newUser.getCreateTime());
        return userVO;

    }

    @Override
    public LoginVO userLogin(UserLoginDTO userLoginDTO) {
        //获取用户名和密码
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //查询用户是否存在
        SysUser user = userMapper.selectOne(
                new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if(user == null){
            throw new BussinessException("用户名或密码错误");
        }

        //比对密码是否正确
        if(!passwordEncoder.matches(password,user.getPassword())){
            throw new BussinessException("用户名或密码错误");
        }

        //没有问题成功登录并且返回前端vo
        LoginVO loginVO = new LoginVO();
        loginVO.setToken("token");
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        loginVO.setRoleName(
                user.getRole() == 1 ? "管理员" : "普通用户"
        );
        return loginVO;
    }

    @Override
    public UserVO currentUserInfoQuery(AdminUserQueryDTO userQueryDTO) {
        return null;
    }

    @Override
    public IPage<UserVO> userInfoQuery(AdminUserQueryDTO userQueryDTO) {
        //取出查询条件
        String username = userQueryDTO.getUsername();
        String studentNumber = userQueryDTO.getStudentNumber();
        String phone = userQueryDTO.getPhone();
        Integer role = userQueryDTO.getRole();
        Long current = userQueryDTO.getCurrent();
        Long size = userQueryDTO.getSize();

        //检查用户权限
        if(role != 1){
            throw new BussinessException("无权限查询其他用户信息");
        }

        //构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(username != null && !username.isEmpty(),SysUser::getUsername, username)
                .eq(studentNumber != null && !studentNumber.isEmpty(),SysUser::getStudentNumber, studentNumber)
                .eq(phone != null && !phone.isEmpty(),SysUser::getPhone, phone)
                .eq(role != null, SysUser::getRole, role);

        IPage<SysUser> page = new Page<>(current, size);
        IPage<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    UserVO vo = new UserVO();
                    vo.setId(user.getId());
                    vo.setUsername(user.getUsername());
                    vo.setStudentNumber(user.getStudentNumber());
                    vo.setPhone(user.getPhone());
                    vo.setRole(user.getRole());
                    vo.setRoleName(user.getRole() == 1 ? "管理员" : "普通用户");
                    vo.setCreateTime(user.getCreateTime());
                    return vo;
                }).collect(Collectors.toList());

        IPage<UserVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(userVOList);
        return resultPage;
    }

    @Override
    public void userInfoUpdate(Long userId, UserUpdateDTO userUpdateDTO) {
        //实现jwt再写
    }

    @Override
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO) {
        //拿取前端传来信息
        String oldPassword = userChangePasswordDTO.getPassword();
        String newPassword = userChangePasswordDTO.getNewPassword();

        //查询用户

    }
}
