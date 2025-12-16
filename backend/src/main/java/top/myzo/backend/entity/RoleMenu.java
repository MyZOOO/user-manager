package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色-菜单关联表实体
 */
@Data
@TableName("t_role_menu")
public class RoleMenu implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long roleId;  // 角色ID
    private Long menuId;  // 菜单ID

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}