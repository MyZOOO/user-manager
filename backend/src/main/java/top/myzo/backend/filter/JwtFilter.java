package top.myzo.backend.filter;

import top.myzo.backend.shiro.JwtToken;
import top.myzo.backend.utils.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class JwtFilter extends BasicHttpAuthenticationFilter {

    /**
     * 执行登录认证
     * 在请求进入时被调用，判断是否需要进行认证
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // 检查请求头中是否有 Authorization token
        if (isLoginAttempt(request, response)) {
            try {
                // 尝试执行登录（验证 token）
                if (!executeLogin(request, response)) {
                    // 登录失败（token 无效或过期），返回 401
                    response401(request, response);
                    return false;
                }
            } catch (Exception e) {
                // token 验证失败，返回 401 未授权
                response401(request, response);
                return false;
            }
        }
        // 无 token 也允许访问（由其他过滤器或注解决定是否需要认证）
        return true;
    }

    /**
     * 判断用户是否想要进行登录（是否提供了 token）
     */
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        // 从 Authorization 请求头中获取 token
        String token = req.getHeader("Authorization");
        // 只有当 token 不为空时才认为用户想要登录
        return StringUtils.hasLength(token);
    }

    /**
     * 执行登录：验证并解析 JWT token
     */
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        // 获取 Authorization 请求头
        String authHeader = httpServletRequest.getHeader("Authorization");
        if (!StringUtils.hasLength(authHeader)) {
            return false;
        }

        // 支持 "Bearer <token>" 或直接 "<token>" 两种格式
        String token = authHeader;
        if (authHeader.startsWith("Bearer ")) {
            // 如果是 "Bearer " 前缀格式，移除前缀获取实际 token
            token = authHeader.substring(7);
        }

        // 创建 JWT token 对象，包装 token 字符串
        JwtToken jwtToken = new JwtToken(token);
        try {
            // 将 token 传递给 Shiro 的 Realm 进行验证和认证
            getSubject(request, response).login(jwtToken);
            return true;
        } catch (Exception e) {
            // token 验证失败（格式错误、过期、无效等）
            return false;
        }
    }

    /**
     * 未授权（401）响应处理
     * 当 token 验证失败或不足以访问该资源时被调用
     */
    private void response401(ServletRequest req, ServletResponse resp) {
        try {
            HttpServletResponse response = (HttpServletResponse) resp;
            // 设置 HTTP 状态码为 401 Unauthorized
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json; charset=utf-8");

            // 构造 JSON 响应体
            PrintWriter out = response.getWriter();
            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(Result.unauthorized("未授权"));
            out.write(json);
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}