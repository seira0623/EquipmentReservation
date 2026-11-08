package org.example.equipmentreservation;

import lombok.RequiredArgsConstructor;
import org.example.equipmentreservation.dto.UserLoginDTO;
import org.example.equipmentreservation.dto.UserRegisterDTO;
import org.example.equipmentreservation.entity.SysUser;
import org.example.equipmentreservation.mapper.UserMapper;
import org.example.equipmentreservation.service.UserService;
import org.example.equipmentreservation.service.impl.UserServiceImpl;
import org.example.equipmentreservation.utils.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.util.Base64;

@SpringBootTest
class EquipmentReservationApplicationTests {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    @Test
    void testInsert() {
        UserRegisterDTO userRegisterDTO = new UserRegisterDTO();
        userRegisterDTO.setUsername("test004");
        userRegisterDTO.setPassword("abc123456");
        userService.userRegister(userRegisterDTO);
    }

    @Test
    void testLogin(){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setUsername("test004");
        userLoginDTO.setPassword("abc123456");
        userService.userLogin(userLoginDTO);
    }

    @Test
    void testJwt(){
        String token = jwtUtil.generateToken(null);
        System.out.println(token);
        System.out.println(jwtUtil.parseUserId(token));
        System.out.println(jwtUtil.isTokenExpired(token));
    }
}

class SecretGenerator {
    public static void main(String[] args) {

        byte[] key = new byte[32];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(key);

        String secret = Base64.getEncoder()
                .encodeToString(key);

        System.out.println(secret);
    }
}