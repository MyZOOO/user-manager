package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_task") // 映射数据库表名 t_task
public class Task implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO) // 主键，自增ID
    private Long id;

    private String taskName; // 任务名称
    private String description; // 任务描述
    private Long initiatorId; // 发起人ID（管理员ID）
    private String initiatorName; // 发起人名称
    private String currentStage; // 当前环节
    private LocalDateTime deadline; // 限定完成时间
    private String completionStatus; // 完成状态：pending(待处理), in_progress(进行中), completed(已完成), rejected(已驳回)
    private String priority; // 优先级：low(低), medium(中), high(高)
    private String remark; // 备注

    // 逻辑删除标记：0=未删除，1=已删除
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    // 时间戳字段
    @TableField(value = "published_time", fill = FieldFill.INSERT) // 发布时间
    private LocalDateTime publishedTime;

    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 统计字段（非数据库字段）
    @TableField(exist = false)
    private Integer assigneeCount; // 分配人数

    @TableField(exist = false)
    private Integer completedCount; // 完成人数

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getInitiatorId() { return initiatorId; }
    public void setInitiatorId(Long initiatorId) { this.initiatorId = initiatorId; }
    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public LocalDateTime getPublishedTime() { return publishedTime; }
    public void setPublishedTime(LocalDateTime publishedTime) { this.publishedTime = publishedTime; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public Integer getAssigneeCount() { return assigneeCount; }
    public void setAssigneeCount(Integer assigneeCount) { this.assigneeCount = assigneeCount; }
    public Integer getCompletedCount() { return completedCount; }
    public void setCompletedCount(Integer completedCount) { this.completedCount = completedCount; }
}
