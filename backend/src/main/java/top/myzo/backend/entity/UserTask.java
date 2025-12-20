package top.myzo.backend.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@TableName("t_user_task") // 映射数据库表名 t_user_task
public class UserTask implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId; // 用户ID
    private String username; // 用户名
    private Long taskId; // 任务ID
    private String taskName; // 任务名称
    private String assignStatus; // 分配状态：assigned(已分配), accepted(已接受), rejected(已拒绝)
    private String completionStatus; // 完成状态：pending(待处理), in_progress(进行中), completed(已完成), rejected(已驳回)
    private LocalDateTime acceptedTime; // 接受时间
    private LocalDateTime completedTime; // 完成时间
    private String completionResult; // 完成结果
    private String remark; // 备注

    // 逻辑删除标记
    @TableLogic
    @TableField(value = "deleted", fill = FieldFill.INSERT)
    private Integer deleted;

    // 时间戳字段
    @TableField(value = "created_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "updated_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    // 关联任务信息（非数据库字段）
    @TableField(exist = false)
    private String description;

    @TableField(exist = false)
    private String priority;

    @TableField(exist = false)
    private String currentStage;

    @TableField(exist = false)
    private LocalDateTime deadline;

    @TableField(exist = false)
    private String initiatorName;

    // Getter/Setter
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    public String getInitiatorName() { return initiatorName; }
    public void setInitiatorName(String initiatorName) { this.initiatorName = initiatorName; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }
    public String getAssignStatus() { return assignStatus; }
    public void setAssignStatus(String assignStatus) { this.assignStatus = assignStatus; }
    public String getCompletionStatus() { return completionStatus; }
    public void setCompletionStatus(String completionStatus) { this.completionStatus = completionStatus; }
    public LocalDateTime getAcceptedTime() { return acceptedTime; }
    public void setAcceptedTime(LocalDateTime acceptedTime) { this.acceptedTime = acceptedTime; }
    public LocalDateTime getCompletedTime() { return completedTime; }
    public void setCompletedTime(LocalDateTime completedTime) { this.completedTime = completedTime; }
    public String getCompletionResult() { return completionResult; }
    public void setCompletionResult(String completionResult) { this.completionResult = completionResult; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getDeleted() { return deleted; }
    public void setDeleted(Integer deleted) { this.deleted = deleted; }
    public LocalDateTime getCreateTime() { return createTime; }
    public void setCreateTime(LocalDateTime createTime) { this.createTime = createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
}
