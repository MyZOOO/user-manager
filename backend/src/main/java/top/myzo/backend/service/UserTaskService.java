package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.UserTask;

import java.util.List;

public interface UserTaskService extends IService<UserTask> {
    /**
     * 分配任务给用户
     */
    boolean assignTaskToUser(Long taskId, Long userId);

    /**
     * 批量分配任务给多个用户
     */
    boolean assignTaskToUsers(Long taskId, List<Long> userIds);

    /**
     * 获取用户的所有任务
     */
    List<UserTask> getUserTasks(Long userId);

    /**
     * 获取任务的所有分配用户
     */
    List<UserTask> getTaskAssignees(Long taskId);

    /**
     * 用户接受任务
     */
    boolean acceptTask(Long userTaskId);

    /**
     * 用户拒绝任务
     */
    boolean rejectTask(Long userTaskId, String reason);

    /**
     * 用户完成任务
     */
    boolean completeTask(Long userTaskId, String result);

    /**
     * 取消任务分配
     */
    boolean removeTaskAssignment(Long userTaskId);

    /**
     * 根据用户ID和任务ID获取分配记录
     */
    UserTask getUserTaskRecord(Long userId, Long taskId);

    /**
     * 更新用户任务状态
     */
    boolean updateUserTaskStatus(Long userTaskId, String status);
}
