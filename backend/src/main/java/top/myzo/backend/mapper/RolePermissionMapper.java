package top.myzo.backend.mapper;

import top.myzo.backend.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {
    // 角色权限关联映射器
}


