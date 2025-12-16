package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.myzo.backend.entity.Permission;
import top.myzo.backend.mapper.PermissionMapper;
import top.myzo.backend.service.PermissionService;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
}

