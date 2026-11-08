package org.example.equipmentreservation.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.equipmentreservation.common.BussinessException;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.dto.*;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.UserService;
import org.example.equipmentreservation.utils.JwtUtil;
import org.example.equipmentreservation.vo.LoginVO;
import org.example.equipmentreservation.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;


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
        newUser.setTokenVersion(0);
        newUser.setAccountStatus(1);
        userMapper.insert(newUser);

        //返回结果
        return getUserVO(newUser);

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

        //账号是否被禁用
        if (!Integer.valueOf(1).equals(user.getAccountStatus())) {
            throw new BussinessException("账号已被禁用");
        }

        //没有问题成功登录并且返回前端vo
        LoginVO loginVO = new LoginVO();
        loginVO.setToken(jwtUtil.generateToken(user.getId(), user.getTokenVersion()));
        loginVO.setUserId(user.getId());
        loginVO.setUsername(user.getUsername());
        loginVO.setRole(user.getRole());
        loginVO.setRoleName(
                user.getRole() == 1 ? "管理员" : "普通用户"
        );
        log.info("用户{}登录成功!", user.getUsername());
        return loginVO;
    }

    @Override
    public UserVO currentUserInfoQuery() {
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException("用户不存在");
        }
        return getUserVO(user);
    }

    @Override
    public IPage<UserVO> userInfoQuery(AdminUserQueryDTO adminuserQueryDTO) {
        //取出查询条件
        Long id = adminuserQueryDTO.getId();
        String username = adminuserQueryDTO.getUsername();
        String studentNumber = adminuserQueryDTO.getStudentNumber();
        String phone = adminuserQueryDTO.getPhone();
        Integer role = adminuserQueryDTO.getRole();
        Long current = adminuserQueryDTO.getCurrent();
        Long size = adminuserQueryDTO.getSize();

        //校验是否为管理员用户
        Long userId = UserContext.getCurrentUserId();
        SysUser isAdmin = userMapper.selectById(userId);
        if(isAdmin == null){
            throw new BussinessException("用户不存在");
        }
        if(!Integer.valueOf(1).equals(isAdmin.getRole())) {
            throw new BussinessException("无权限查询其他用户信息");
        }

        //构建查询条件
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(id != null, SysUser::getId, id)
                .like(username != null && !username.isBlank(),SysUser::getUsername, username)
                .eq(studentNumber != null && !studentNumber.isBlank(),SysUser::getStudentNumber, studentNumber)
                .eq(phone != null && !phone.isBlank(),SysUser::getPhone, phone)
                .eq(role != null, SysUser::getRole, role)
                .orderByDesc(SysUser::getCreateTime)
                .orderByDesc(SysUser::getId);

        IPage<SysUser> page = new Page<>(current, size);
        IPage<SysUser> userPage = userMapper.selectPage(page, queryWrapper);
        List<UserVO> userVOList = userPage.getRecords().stream()
                .map(user -> {
                    return getUserVO(user);
                }).collect(Collectors.toList());

        IPage<UserVO> resultPage = new Page<>(page.getCurrent(), page.getSize(), page.getTotal());
        resultPage.setRecords(userVOList);
        return resultPage;
    }

    @Nonnull
    private static UserVO getUserVO(SysUser user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setStudentNumber(user.getStudentNumber());
        vo.setPhone(user.getPhone());
        vo.setRole(user.getRole());
        vo.setRoleName(user.getRole() == 1 ? "管理员" : "普通用户");
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    @Override
    public void userInfoUpdate(UserUpdateDTO userUpdateDTO) {
        String phone = userUpdateDTO.getPhone();
        if (phone == null || phone.isBlank()) {
            throw new BussinessException("手机号不能为空");
        }
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException("用户不存在");
        }
        user.setPhone(phone);
        userMapper.updateById(user);
        log.info("用户{}修改手机号成功!", user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(UserChangePasswordDTO userChangePasswordDTO) {
        //拿取前端传来信息
        String oldPassword = userChangePasswordDTO.getPassword();
        String newPassword = userChangePasswordDTO.getNewPassword();

        //查询用户
        Long userId = UserContext.getCurrentUserId();
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BussinessException("用户不存在");
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BussinessException("原密码错误");
        }
        if(passwordEncoder.matches(newPassword, user.getPassword())){
            throw new BussinessException("新密码不能与原密码相同");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setTokenVersion(user.getTokenVersion() + 1);
        userMapper.updateById(user);

        log.info("用户{}修改密码成功!", user.getUsername());
    }
}
