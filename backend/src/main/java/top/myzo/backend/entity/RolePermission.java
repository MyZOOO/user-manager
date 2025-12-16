package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色-权限关联表实体
 */
@Data
@TableName("t_role_permission")
public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;        // 角色ID
    private Long permissionId;  // 权限ID

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}