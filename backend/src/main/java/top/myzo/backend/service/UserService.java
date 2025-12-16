package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.User;

import java.util.List;

public interface UserService extends IService<User> {
    // 基础认证方法
    User getUserByUsername(String username);
    User registerUser(User user);
    boolean validatePassword(String rawPassword, String encodedPassword, String salt);
    String encodePassword(String password, String salt);
    String generateSalt();
    
    // 用户管理方法
    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否成功
     */
    boolean changePassword(Long userId, String oldPassword, String newPassword);
    
    /**
     * 更新用户信息（不包括密码和角色）
     * @param user 用户对象
     * @return 更新后的用户
     */
    User updateUserInfo(User user);
    
    /**
     * 管理员修改用户角色
     * @param userId 用户ID
     * @param role 新角色
     * @return 是否成功
     */
    boolean updateUserRole(Long userId, String role);
    
    /**
     * 按用户名模糊搜索用户
     * @param username 用户名关键字
     * @return 用户列表
     */
    List<User> searchUsersByUsername(String username);
}