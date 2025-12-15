package top.myzo.backend.controller;

import top.myzo.backend.entity.User;
import top.myzo.backend.service.UserService;
import top.myzo.backend.utils.JwtUtil;
import top.myzo.backend.utils.Result;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired // 依赖注入用户服务
    private UserService userService;

    @Autowired // 依赖注入 JWT 工具类
    private JwtUtil jwtUtil;

    /**
     * 用户登录端点
     * 接受用户名和密码，验证后返回 JWT token
     */
    @PostMapping("/login")
    public Result login(@RequestParam String username, @RequestParam String password) {

        // 从数据库查询用户是否存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 验证密码是否正确（使用 SHA-256 + 盐值验证）
        if (!userService.validatePassword(password, user.getPassword(), user.getSalt())) {
            return Result.error("密码错误");
        }

        // 根据用户名生成 JWT token（有效期 24 小时）
        String token = jwtUtil.generateToken(user.getUsername());

        // 构造登录成功响应，包含 token 和用户信息
        Map<String, Object> result = new HashMap<>();
        result.put("token", token); // JWT token，用于后续请求的认证
        result.put("user", user); // 用户信息（包含 id、username、email、age、role 等）

        // 返回登录成功的结果
        return Result.success("登录成功", result);
    }

    /**
     * 用户注册端点
     * 接受用户信息（用户名、密码、邮箱、年龄），创建新用户
     */
    @PostMapping("/register")
    public Result register(@RequestBody User user) {

        // 验证用户名是否已存在
        User existingUser = userService.getUserByUsername(user.getUsername());
        if (existingUser != null) {
            return Result.error("用户名已存在");
        }

        // 调用服务注册用户
        // UserServiceImpl.registerUser 会自动处理：
        // 1. 生成随机盐值
        // 2. 使用 SHA-256 + 盐值加密密码
        // 3. 设置默认角色为 "user"
        // 4. 设置 deleted = 0（未删除）
        User registeredUser = userService.registerUser(user);
        
        // 返回注册成功的结果，包含新用户信息
        return Result.success("注册成功", registeredUser);
    }

    /**
     * 用户登出端点
     * 登出当前用户（清除认证信息）
     */
    @PostMapping("/logout")
    public Result logout() {
        // 调用 Shiro 的 logout 方法清除认证信息
        // 由于我们使用 JWT 无状态认证，实际的 token 验证在服务端进行
        // 客户端只需要停止使用该 token 即可
        SecurityUtils.getSubject().logout();
        
        // 返回登出成功的结果
        return Result.success("登出成功");
    }
}