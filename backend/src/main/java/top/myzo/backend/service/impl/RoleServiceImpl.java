package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.myzo.backend.entity.Role;
import top.myzo.backend.mapper.RoleMapper;
import top.myzo.backend.service.RoleService;

@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}

