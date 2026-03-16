package top.myzo.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.myzo.backend.entity.Permission;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {
    @Select("select p.code from t_permission p join t_user_permission up on p.id = up.permission_id where up.user_id = #{userId}")
    String getPermissionByUserId(Long userId);
}

