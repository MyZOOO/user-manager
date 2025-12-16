package top.myzo.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.myzo.backend.entity.UserRole;

@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {
    // 用户角色关联映射器
}
