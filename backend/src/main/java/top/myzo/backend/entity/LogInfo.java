package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_log_info")
public class LogInfo {
    @TableId(value = "id", type = IdType.AUTO) // 主键，自增ID
    private Long id;
    private String username;
    private String ipAddress;
    private String os;
    private String status;
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
