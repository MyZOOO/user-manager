package top.myzo.backend.config;

import org.apache.shiro.mgt.SecurityManager;
import top.myzo.backend.filter.JwtFilter;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import top.myzo.backend.shiro.JwtRealm;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration // Spring 配置类注解
public class ShiroConfig {

    /**
     * 注册 JWT 过滤器 Bean
     * 此过滤器用于处理 HTTP 请求中的 JWT token 验证
     */
    @Bean
    public JwtFilter jwtFilter() {
        // 创建 JWT 过滤器实例（不使用 @Component 注解，由此方法提供）
        return new JwtFilter();
    }

    /**
     * 配置 Shiro 的安全管理器
     * SecurityManager 是 Shiro 的核心组件，负责管理认证和授权
     */
    @Bean
    public DefaultWebSecurityManager securityManager(JwtRealm jwtRealm) {
        // 创建 Web 安全管理器
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        
        // 设置 Realm（自定义 JwtRealm）
        securityManager.setRealm(jwtRealm);

        // 配置无状态认证（不使用 session，适合 JWT）
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        // 禁用 session 存储，因为 JWT 是无状态的
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator);
        // 将配置应用到 SecurityManager
        securityManager.setSubjectDAO(subjectDAO);

        return securityManager;
    }

    /**
     * 配置 Shiro 过滤器工厂
     * 此工厂负责创建过滤器链，定义哪些路径需要认证、授权等
     */
    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, JwtFilter jwtFilter) {
        // 创建过滤器工厂 Bean
        ShiroFilterFactoryBean shiroFilter = new ShiroFilterFactoryBean();
        // 将安全管理器设置到过滤器工厂
        shiroFilter.setSecurityManager(securityManager);

        // 注册自定义的 JWT 过滤器到 Shiro
        Map<String, Filter> filters = new HashMap<>();
        filters.put("jwt", jwtFilter); // 注册为 "jwt" 名称，在过滤链中使用
        shiroFilter.setFilters(filters);

        // 定义过滤链规则：URL 路径匹配与过滤器映射
        Map<String, String> filterChainDefinitionMap = new HashMap<>();
        // /auth/** 路径：anon（匿名可访问，无需认证）- 用于登录、注册、登出
        filterChainDefinitionMap.put("/auth/**", "anon");
        // /** 路径：jwt（需要 JWT 认证）- 其他所有路径需要 JWT token 验证
        filterChainDefinitionMap.put("/**", "jwt");

        // 将过滤链定义应用到过滤器工厂
        shiroFilter.setFilterChainDefinitionMap(filterChainDefinitionMap);

        return shiroFilter;
    }

    /**
     * Shiro 生命周期管理器
     * 负责 Shiro 的初始化和销毁
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 权限注解处理顾问
     * 使 @RequiresRoles、@RequiresPermissions 等注解生效
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        // 创建权限注解处理顾问
        AuthorizationAttributeSourceAdvisor advisor = new AuthorizationAttributeSourceAdvisor();
        // 将安全管理器设置到顾问中
        advisor.setSecurityManager(securityManager);
        return advisor;
    }
}