package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.myzo.backend.entity.Permission;
import top.myzo.backend.mapper.PermissionMapper;
import top.myzo.backend.mapper.UserMapper;
import top.myzo.backend.service.PermissionService;

import javax.annotation.Resource;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    @Resource
    PermissionMapper permissionMapper;

    @Resource
    UserMapper userMapper;
    @Override
    public String getPermissionByUserId(Long userId) {
        if (userMapper.selectById(userId) == null) {
            throw new IllegalArgumentException("用户不存在");
        }
        return permissionMapper.getPermissionByUserId(userId);
    }
}