package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@TableName("t_role")
public class Role {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private String name;
    private String code;
    private String description;
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

    // 显式 getter/setter，避免 Lombok 失效导致的编译问题
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
