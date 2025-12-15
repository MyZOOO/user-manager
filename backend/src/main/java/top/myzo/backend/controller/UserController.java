package top.myzo.backend.controller;

import top.myzo.backend.entity.User;
import top.myzo.backend.service.UserService;
import top.myzo.backend.utils.JwtUtil;
import top.myzo.backend.utils.Result;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired // 依赖注入用户服务
    private UserService userService;

    @Autowired // 依赖注入 JWT 工具类
    private JwtUtil jwtUtil;

    /**
     * 获取当前登录用户的信息
     * @RequiresAuthentication 注解：要求用户必须已认证（已登录）
     */
    @RequiresAuthentication // 只允许已认证的用户访问
    @GetMapping("/current")
    public Result getCurrentUser() {
        // 从 Shiro 的 Subject 中获取 principal（我们存储的是 JWT token）
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            return Result.unauthorized("未登录");
        }
        
        // 将 principal 转换为 token 字符串
        String token = principal.toString();
        // 从 token 中解析 JWT 声明（包含用户名和其他信息）
        Claims claims = jwtUtil.getClaimByToken(token);

        // 验证 token 有效性
        if (claims == null) {
            return Result.error("Token 无效");
        }

        // 从 JWT 声明中提取用户名（Subject）
        String username = claims.getSubject();
        if (username == null) {
            return Result.error("无法解析用户");
        }

        // 从数据库查询用户完整信息（包括 role 等字段）
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 返回当前用户信息
        return Result.success("获取成功", user);
    }

    /**
     * 获取所有用户列表
     * @RequiresRoles("admin") 注解：只允许具有 admin 角色的用户访问
     * 普通用户访问时会返回 401 权限不足错误
     */
    @RequiresRoles("admin") // 只允许 admin 角色的用户访问，其他用户返回 401
    @GetMapping("/list")
    public Result getUserList() {
        // 从数据库查询所有未删除的用户列表
        List<User> users = userService.list();
        return Result.success("获取成功", users);
    }

    /**
     * 根据 ID 获取指定用户信息
     * @RequiresRoles("admin") 注解：只允许具有 admin 角色的用户访问
     */
    @RequiresRoles("admin") // 只允许 admin 角色的用户访问，其他用户返回 401
    @GetMapping("/{id}")
    public Result getUserById(@PathVariable Long id) {
        // 首先尝试按 ID 查询用户（MyBatis-Plus 会自动过滤逻辑删除的记录）
        User user = userService.getById(id);
        if (user == null) {
            // 如果没有找到，手动构造查询条件，兼容 deleted 列为 NULL 的情况
            // 这是为了处理旧数据中 deleted 字段可能为 NULL 的情况
            QueryWrapper<User> qw = new QueryWrapper<>();
            qw.eq("id", id) // 匹配指定 ID
              .and(w -> w.eq("deleted", 0) // deleted = 0（未删除）
                        .or() // 或者
                        .isNull("deleted")); // deleted 为 NULL（向后兼容）
            user = userService.getOne(qw);
        }
        
        // 如果仍然没有找到，返回错误
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 返回指定用户的信息
        return Result.success("获取成功", user);
    }

    @RequiresRoles("admin")
    @DeleteMapping("/delete")
    public Result deleteUserById(@RequestParam Long id) {
        // 验证ID是否有效
        if (id == null || id <= 0) {
            return Result.error("用户ID无效");
        }
        // 验证用户是否存在
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 软删除用户
        user.setDeleted(1);
        if (!userService.updateById(user)) {
            return Result.error("用户删除失败");
        } else {
            return Result.success("用户已删除");
        }
    }

    @RequiresRoles("admin")
    @PostMapping("/authorize")
    public Result authorize(@RequestParam Long id) {
        // 验证ID是否有效
        if (id == null || id <= 0) {
            return Result.error("用户ID无效");
        }
        // 验证用户是否存在
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        // 给用户授权角色
        user.setRole("admin");
        if (!userService.updateById(user)) {
            return Result.error("用户授权失败");
        } else {
            return Result.success("用户已授权管理员权限");
        }
    }

    public Result update(@RequestBody User user) {
        // 更新用户信息
        if (user.getId() == null || user.getId() <= 0) {
            return Result.error("用户ID无效");
        }
        User existingUser = userService.getById(user.getId());
        if (existingUser == null) {
            return Result.error("用户不存在");
        }
        // 只更新允许修改的字段
        existingUser.setEmail(user.getEmail());
        existingUser.setAge(user.getAge());
        if (!userService.updateById(existingUser)) {
            return Result.error("用户更新失败");
        } else {
            return Result.success("用户信息已更新", existingUser);
        }
    }
}