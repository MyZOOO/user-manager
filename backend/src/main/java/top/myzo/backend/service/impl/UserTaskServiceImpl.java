package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.myzo.backend.entity.Task;
import top.myzo.backend.entity.UserTask;
import top.myzo.backend.mapper.TaskMapper;
import top.myzo.backend.mapper.UserTaskMapper;
import top.myzo.backend.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserTaskServiceImpl extends ServiceImpl<UserTaskMapper, UserTask> implements UserTaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Override
    public boolean assignTaskToUser(Long taskId, Long userId) {
        UserTask userTask = new UserTask();
        userTask.setTaskId(taskId);
        userTask.setUserId(userId);
        userTask.setAssignStatus("assigned");
        userTask.setCompletionStatus("pending");
        boolean result = this.save(userTask);
        if (result) {
            updateParentTaskStatus(taskId);
        }
        return result;
    }

    @Override
    public boolean assignTaskToUsers(Long taskId, List<Long> userIds) {
        for (Long userId : userIds) {
            assignTaskToUser(taskId, userId);
        }
        return true;
    }

    @Override
    public List<UserTask> getUserTasks(Long userId) {
        return this.baseMapper.getTasksByUserId(userId);
    }

    @Override
    public List<UserTask> getTaskAssignees(Long taskId) {
        return this.baseMapper.getUsersByTaskId(taskId);
    }

    @Override
    public boolean acceptTask(Long userTaskId) {
        UserTask userTask = this.getById(userTaskId);
        if (userTask == null) return false;
        
        userTask.setAssignStatus("accepted");
        userTask.setAcceptedTime(LocalDateTime.now());
        boolean result = this.updateById(userTask);
        if (result) {
            updateParentTaskStatus(userTask.getTaskId());
        }
        return result;
    }

    @Override
    public boolean rejectTask(Long userTaskId, String reason) {
        UserTask userTask = this.getById(userTaskId);
        if (userTask == null) return false;

        userTask.setAssignStatus("rejected");
        userTask.setRemark(reason);
        userTask.setCompletionStatus("rejected"); // 同时更新完成状态
        boolean result = this.updateById(userTask);
        if (result) {
            updateParentTaskStatus(userTask.getTaskId());
        }
        return result;
    }

    @Override
    public boolean completeTask(Long userTaskId, String result) {
        UserTask userTask = this.getById(userTaskId);
        if (userTask == null) return false;

        userTask.setCompletionStatus("completed");
        userTask.setCompletedTime(LocalDateTime.now());
        userTask.setCompletionResult(result);
        boolean success = this.updateById(userTask);
        if (success) {
            updateParentTaskStatus(userTask.getTaskId());
        }
        return success;
    }

    @Override
    public boolean removeTaskAssignment(Long userTaskId) {
        UserTask userTask = this.getById(userTaskId);
        boolean result = this.removeById(userTaskId);
        if (result && userTask != null) {
            updateParentTaskStatus(userTask.getTaskId());
        }
        return result;
    }

    @Override
    public UserTask getUserTaskRecord(Long userId, Long taskId) {
        return this.baseMapper.getUserTask(userId, taskId);
    }

    @Override
    public boolean updateUserTaskStatus(Long userTaskId, String status) {
        UserTask userTask = this.getById(userTaskId);
        if (userTask == null) return false;

        userTask.setCompletionStatus(status);
        boolean result = this.updateById(userTask);
        if (result) {
            updateParentTaskStatus(userTask.getTaskId());
        }
        return result;
    }

    /**
     * 更新父任务状态
     */
    private void updateParentTaskStatus(Long taskId) {
        QueryWrapper<UserTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("task_id", taskId);
        queryWrapper.eq("deleted", 0);
        List<UserTask> userTasks = this.list(queryWrapper);

        if (userTasks.isEmpty()) {
            // 如果没有分配给任何人，状态重置为 pending
            Task task = new Task();
            task.setId(taskId);
            task.setCompletionStatus("pending");
            taskMapper.updateById(task);
            return;
        }

        int total = userTasks.size();
        int completed = 0;
        int inProgress = 0;
        int rejected = 0;

        for (UserTask ut : userTasks) {
            String status = ut.getCompletionStatus();
            if ("completed".equals(status)) {
                completed++;
            } else if ("in_progress".equals(status)) {
                inProgress++;
            } else if ("rejected".equals(status)) {
                rejected++;
            }
        }

        String newStatus = "pending";
        if (completed == total) {
            newStatus = "completed";
        } else if (rejected == total) {
            newStatus = "rejected";
        } else if (completed > 0 || inProgress > 0) {
            newStatus = "in_progress";
        }

        Task task = new Task();
        task.setId(taskId);
        task.setCompletionStatus(newStatus);
        taskMapper.updateById(task);
    }
}
