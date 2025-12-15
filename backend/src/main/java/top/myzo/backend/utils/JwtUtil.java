package top.myzo.backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire}")
    private Long expire;

    @Value("${jwt.header}")
    private String header;

    /**
     * 生成JWT token
     */
    public String generateToken(String username) {

        // 生成过期时间
        Date nowDate = new Date();
        Date expireDate = new Date(nowDate.getTime() + expire * 1000);

        return Jwts.builder()
                .setHeaderParam("type", "JWT") // 设定头部参数
                .setSubject(username) // 设定主体
                .setIssuedAt(nowDate) // 设定发布时间
                .setExpiration(expireDate) // 设定过期时间
                .signWith(SignatureAlgorithm.HS512, secret) // 加密算法签名密钥
                .compact();
    }

    /**
     * 解析JWT token
     */
    public Claims getClaimByToken(String token) {
        try {
            return Jwts.parser() // 创建解析器
                    .setSigningKey(secret) // 设置签名密钥
                    .parseClaimsJws(token) // 解析并校验
                    .getBody(); // 提取载荷
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * token是否过期
     */
    public boolean isTokenExpired(Date expiration) {
        return expiration.before(new Date());
    }
}