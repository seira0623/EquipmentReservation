package org.example.equipmentreservation.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private static final long JWT_EXPIRE = 2 * 60 * 60 * 1000;

    @Value("${JWT_SECRET}")
    private String secret;

    private SecretKey JWT_SECRET;

    /**
     * 初始化密钥，把环境变量中的字符串传为SecreKey格式
     * hs256算法需要32字节密钥
     */
    @PostConstruct
    public void init(){
        if(secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32){
            throw new IllegalStateException("密钥为空或长度小于32字节");
        }

        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.JWT_SECRET = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成token
     * @param userId
     * @return token
     */
    public String generateToken(Long userId){
        if (userId == null) {
            throw new IllegalArgumentException("用户ID为空");
        }
        Long now = System.currentTimeMillis();
        return Jwts.builder()
                .claim("userId", userId) //放进token的信息，只存入id
                .issuedAt(new Date(now)) //签发时间
                .expiration(new Date(now + JWT_EXPIRE)) //过期时间
                .signWith(JWT_SECRET, Jwts.SIG.HS256) //使用HS256算法和密钥进行签名
                .compact();
    }

    /**
     * 从token中解析出用户id
     * @param token
     * @return 用户id
     */
    public Long parseUserId(String token){
        Claims claims = Jwts.parser()
                .verifyWith(JWT_SECRET)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", Long.class);
    }

    /**
     * 验证token
     * @param token
     * @return 是否合法
     */
    public boolean validateToken(String token){
        if (token == null || token.isBlank()) {
            return false;
        }

        try{
            parseUserId(token);
            return true;
        } catch (JwtException | IllegalArgumentException exception){
            return false;
        }
    }

    /**
     * 判断token是否过期
     * @param token
     * @return 是否过期
     */
    public Boolean isTokenExpired(String token){
        if (token == null || token.isBlank()) {
            return false;
        }
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(JWT_SECRET)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getExpiration().before(new Date());
        } catch (JwtException| IllegalArgumentException e) {
            return true;
        }
    }

}
