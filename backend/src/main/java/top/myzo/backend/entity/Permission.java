package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_permission")
public class Permission {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;              // 主键ID（修正为小写 id）

    private String name;          // 权限名称（修正类型为 String）
    private String code;          // 权限编码
    private String type;          // 类型：menu/button/api 等
    private String path;          // 资源路径
    private String method;        // HTTP 方法（GET/POST/...）

    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 显式 getter/setter，避免 Lombok 失效导致的编译问题
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
}
