package org.example.equipmentreservation.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.common.UserContext;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.utils.JwtUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        //从请求头拿到token
        String path = request.getRequestURI();
        String token = request.getHeader("Authorization");

        //校验token
        if(token == null || !token.startsWith("Bearer ")){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "缺少有效的登陆凭证");
            return false;
        }
        token = token.substring(7).trim();

        //验证token
        if(!jwtUtil.validateToken(token)){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "无效的登陆凭证");
            return false;
        }

        //从token中获取用户id，用id查询用户
        Long userId = jwtUtil.parseUserId(token);
        SysUser user = userMapper.selectById(userId);
        if(user == null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "用户不存在");
            return false;
        }
        if(!Integer.valueOf(1).equals(user.getAccountStatus())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "账号已被禁用");
            return false;
        }

        //检验版本号
        if(!Objects.equals(user.getTokenVersion(), jwtUtil.parseTokenVersion(token))){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token已失效，请重新登录");
            return false;
        }

        //往localThread中存用户id
        UserContext.setCurrentUserId(user.getId());
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        UserContext.remove();
    }
}
