package top.myzo.backend.shiro;

import top.myzo.backend.entity.User;
import top.myzo.backend.service.UserService;
import top.myzo.backend.utils.JwtUtil;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component // Spring 组件，Shiro 自动注册
public class JwtRealm extends AuthorizingRealm {

    @Resource // 注入 JWT 工具类
    private JwtUtil jwtUtil;

    @Resource // 注入用户服务
    private UserService userService;

    @Override
    public boolean supports(AuthenticationToken token) {
        // 指定此 Realm 只处理 JwtToken 类型的认证请求
        return token instanceof JwtToken;
    }

    /**
     * 授权方法：根据用户角色分配权限
     * 在使用 @RequiresRoles 或 @RequiresPermissions 注解时被调用
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        // 创建授权信息对象，用于存储用户的角色和权限
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        
        // 从 principals 中获取主体（认证时存入的 JWT token）
        Object primary = principals.getPrimaryPrincipal();
        if (primary == null) {
            return authorizationInfo;
        }
        
        // token 被作为 principal 存储
        String token = primary.toString();
        
        // 通过 JWT token 解析用户名
        String username = null;
        if (jwtUtil.getClaimByToken(token) != null) {
            // 从 token 的 Subject 声明中获取用户名
            username = jwtUtil.getClaimByToken(token).getSubject();
        }

        // 如果用户名为空，无法分配权限
        if (username == null) {
            return authorizationInfo;
        }

        // 从数据库查询用户信息，获取用户的角色（比在 token 中存储角色更可靠）
        User user = userService.getUserByUsername(username);
        if (user == null) {
            return authorizationInfo;
        }
        
        // 获取用户角色，默认为普通用户 "user"
        String role = user.getRole();
        if (role == null) {
            role = "user";
        }

        // 根据用户角色分配相应的权限
        if ("admin".equals(role)) {
            // 管理员角色：赋予 "admin" 角色标识
            authorizationInfo.addRole("admin");
            // 管理员有 user:read（读取用户）和 user:write（修改用户）权限
            authorizationInfo.addStringPermission("user:read");
            authorizationInfo.addStringPermission("user:write");
        } else {
            // 普通用户角色：赋予 "user" 角色标识
            authorizationInfo.addRole("user");
            // 普通用户只有 user:read（读取自己信息）权限
            authorizationInfo.addStringPermission("user:read");
        }

        return authorizationInfo;
    }

    /**
     * 认证方法：验证用户身份
     * 在用户发送 JWT token 时被调用
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 从认证 token 中提取 JWT 字符串
        String token = (String) authenticationToken.getPrincipal();

        // 验证 token 不为空且能被正确解析
        if (token == null || jwtUtil.getClaimByToken(token) == null) {
            throw new AuthenticationException("Token失效，请重新登录");
        }

        // 从 token 中提取用户名（Subject 声明）
        String username = jwtUtil.getClaimByToken(token).getSubject();
        if (username == null) {
            throw new AuthenticationException("Token异常");
        }

        // 从数据库查询用户，确保用户仍存在
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new AuthenticationException("用户不存在");
        }

        // 验证 token 是否过期（比较过期时间与当前时间）
        if (jwtUtil.isTokenExpired(jwtUtil.getClaimByToken(token).getExpiration())) {
            throw new AuthenticationException("Token已过期，请重新登录");
        }

        // 认证成功：返回认证信息，包含 token 作为 principal
        // 第一个参数是 principal（用于授权时取出），第二个参数是 credential（这里也是 token），第三个是 realm 名字
        return new SimpleAuthenticationInfo(token, token, "jwt_realm");
    }
}