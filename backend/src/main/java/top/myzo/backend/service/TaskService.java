package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.Task;

import java.util.List;

public interface TaskService extends IService<Task> {
    /**
     * 管理员发布任务
     */
    boolean publishTask(Task task);

    /**
     * 获取管理员发布的所有任务
     */
    List<Task> getTasksByInitiator(Long initiatorId);

    /**
     * 根据完成状态获取任务
     */
    List<Task> getTasksByStatus(String completionStatus);

    /**
     * 更新任务状态
     */
    boolean updateTaskStatus(Long taskId, String status);

    /**
     * 更新当前环节
     */
    boolean updateCurrentStage(Long taskId, String stage);

    /**
     * 删除任务
     */
    boolean removeTask(Long taskId);

    /**
     * 获取所有任务列表
     */
    List<Task> getAllTasks();

    /**
     * 根据任务ID获取任务详情
     */
    Task getTaskDetail(Long taskId);
}
