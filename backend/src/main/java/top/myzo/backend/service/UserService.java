package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.User;

public interface UserService extends IService<User> {
    User getUserByUsername(String username);
    User registerUser(User user);
    boolean validatePassword(String rawPassword, String encodedPassword, String salt);
    String encodePassword(String password, String salt);
    String generateSalt();
}