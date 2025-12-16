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
import java.util.Map;
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

    /**
     * 更新用户信息（用户本人或管理员）
     * 允许用户修改自己的邮箱和年龄，管理员可以修改任何用户
     */
    @RequiresAuthentication
    @PutMapping("/{id}")
    public Result updateUser(@PathVariable Long id, @RequestBody User user) {
        // 从 token 获取当前登录用户
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            return Result.unauthorized("未登录");
        }
        
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        String currentUsername = claims.getSubject();
        User currentUser = userService.getUserByUsername(currentUsername);
        
        // 检查权限：只能修改自己的信息，或者是管理员
        if (!currentUser.getId().equals(id) && !"admin".equals(currentUser.getRole())) {
            return Result.error("权限不足：只能修改自己的信息");
        }
        
        // 设置ID并更新
        user.setId(id);
        User updatedUser = userService.updateUserInfo(user);
        
        if (updatedUser == null) {
            return Result.error("用户不存在");
        }
        
        return Result.success("更新成功", updatedUser);
    }

    /**
     * 修改密码（用户本人）
     * 需要验证旧密码后才能修改
     */
    @RequiresAuthentication
    @PostMapping("/{id}/password")
    public Result changePassword(@PathVariable Long id, 
                                @RequestBody Map<String, String> passwords) {
        // 从 token 获取当前登录用户
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            return Result.unauthorized("未登录");
        }
        
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        String currentUsername = claims.getSubject();
        User currentUser = userService.getUserByUsername(currentUsername);
        
        // 检查权限：只能修改自己的密码
        if (!currentUser.getId().equals(id)) {
            return Result.error("权限不足：只能修改自己的密码");
        }
        
        String oldPassword = passwords.get("oldPassword");
        String newPassword = passwords.get("newPassword");
        
        if (oldPassword == null || newPassword == null) {
            return Result.error("参数错误：需要提供 oldPassword 和 newPassword");
        }
        
        if (newPassword.length() < 6) {
            return Result.error("新密码长度不能少于6位");
        }
        
        boolean success = userService.changePassword(id, oldPassword, newPassword);
        
        if (!success) {
            return Result.error("修改失败：旧密码不正确或用户不存在");
        }
        
        return Result.success("密码修改成功");
    }

    /**
     * 删除用户（软删除，仅管理员）
     */
    @RequiresRoles("admin")
    @DeleteMapping("/{id}")
    public Result deleteUser(@PathVariable Long id) {
        // 验证用户是否存在
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        // 软删除用户（MyBatis-Plus 的 removeById 会自动设置 deleted = 1）
        boolean success = userService.removeById(id);
        
        if (!success) {
            return Result.error("删除失败");
        }
        
        return Result.success("用户已删除");
    }

    /**
     * 分配角色（仅管理员）
     * 可以将用户设置为 admin 或 user 角色
     */
    @RequiresRoles("admin")
    @PostMapping("/{id}/role")
    public Result assignRole(@PathVariable Long id, 
                            @RequestBody Map<String, String> roleData) {
        String role = roleData.get("role");
        
        if (role == null) {
            return Result.error("参数错误：需要提供 role 字段");
        }
        
        boolean success = userService.updateUserRole(id, role);
        
        if (!success) {
            return Result.error("分配失败：用户不存在或角色值不合法（只能是 admin 或 user）");
        }
        
        return Result.success("角色分配成功");
    }

    /**
     * 搜索用户（按用户名模糊查询，仅管理员）
     */
    @RequiresRoles("admin")
    @GetMapping("/search")
    public Result searchUsers(@RequestParam String username) {
        if (username == null || username.trim().isEmpty()) {
            return Result.error("参数错误：username 不能为空");
        }
        
        List<User> users = userService.searchUsersByUsername(username);
        return Result.success("搜索成功", users);
    }

    // ========== 已废弃的端点（保留兼容性）==========

    /**
     * @deprecated 请使用 DELETE /{id}
     */
    @Deprecated
    @RequiresRoles("admin")
    @DeleteMapping("/delete")
    public Result deleteUserById(@RequestParam Long id) {
        return deleteUser(id);
    }

    /**
     * @deprecated 请使用 POST /{id}/role
     */
    @Deprecated
    @RequiresRoles("admin")
    @PostMapping("/authorize")
    public Result authorize(@RequestParam Long id) {
        User user = userService.getById(id);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        user.setRole("admin");
        if (!userService.updateById(user)) {
            return Result.error("用户授权失败");
        }
        
        return Result.success("用户已授权管理员权限");
    }

    /**
     * @deprecated 请使用 PUT /{id}
     */
    @Deprecated
    @RequiresAuthentication
    @PostMapping("/update")
    public Result update(@RequestBody User user) {
        if (user.getId() == null || user.getId() <= 0) {
            return Result.error("用户ID无效");
        }
        return updateUser(user.getId(), user);
    }
}