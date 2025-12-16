package top.myzo.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.myzo.backend.entity.Permission;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
}

