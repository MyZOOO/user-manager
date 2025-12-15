package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_user") // 映射数据库表名 t_user
public class User implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO) // 主键，自增ID
    private Long id;

    // 用户基本信息
    private String username; // 用户名
    private String password; // 加密后的密码
    private String salt; // 密码盐值，用于加密
    private String email; // 邮箱
    private Integer age; // 年龄

    // 逻辑删除标记：0=未删除，1=已删除
    @TableLogic // MyBatis-Plus 逻辑删除注解
    @TableField(value = "deleted", fill = FieldFill.INSERT) // 插入时自动填充
    private Integer deleted;

    // 时间戳字段：数据库字段名 created_time，Java属性名 createTime
    @TableField(value = "created_time", fill = FieldFill.INSERT) // 创建时自动填充当前时间
    private LocalDateTime createTime;

    // 修改时间戳：插入或更新时自动填充
    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE) // 创建或更新时自动填充当前时间
    private LocalDateTime updateTime;

    /**
     * 用户角色：'user'=普通用户，'admin'=管理员
     * 用于权限控制和访问限制
     */
    private String role; // 角色字段，决定用户权限级别
}