package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.myzo.backend.entity.User;
import top.myzo.backend.mapper.UserMapper;
import top.myzo.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service // Spring 服务组件注解
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User getUserByUsername(String username) {
        // 调用 MyBatis mapper 通过用户名查询用户信息
        return this.baseMapper.selectByUsername(username);
    }

    @Override
    public User registerUser(User user) {
        // 生成随机盐值（用于增强密码安全性）
        String salt = generateSalt();
        user.setSalt(salt);

        // 使用 SHA-256 + 盐值加密密码
        String encodedPassword = encodePassword(user.getPassword(), salt);
        user.setPassword(encodedPassword);

        // 确保逻辑删除字段设为 0（未删除），防止 MyBatis-Plus 逻辑删除过滤掉新注册用户
        user.setDeleted(0);
        
        // 新注册用户默认角色为普通用户，不是管理员
        if (user.getRole() == null) {
            user.setRole("user");
        }

        // 保存用户到数据库
        this.save(user);
        return user;
    }

    @Override
    public boolean validatePassword(String rawPassword, String encodedPassword, String salt) {
        // 使用相同的盐值和算法重新加密输入的密码
        String encodedInput = encodePassword(rawPassword, salt);
        // 比较加密后的密码是否相等
        return encodedInput.equals(encodedPassword);
    }

    @Override
    public String encodePassword(String password, String salt) {
        try {
            // 创建 SHA-256 消息摘要实例
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            // 将密码和盐值组合在一起，增强密码强度
            String saltedPassword = password + salt;
            
            // 计算组合字符串的 SHA-256 哈希
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());

            // 将字节数组转换为十六进制字符串
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                // 使用 %02x 格式确保每个字节都显示为两位十六进制数
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 是标准算法，不应该出现异常
            throw new RuntimeException("Error encoding password", e);
        }
    }

    @Override
    public String generateSalt() {
        // 使用 UUID 生成 32 位随机盐值（去掉连字符）
        return UUID.randomUUID().toString().replace("-", "");
    }
}