package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.myzo.backend.entity.Task;
import top.myzo.backend.mapper.TaskMapper;
import top.myzo.backend.service.TaskService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Override
    public boolean publishTask(Task task) {
        if (task == null || task.getTaskName() == null) {
            return false;
        }
        // 设置默认值
        if (task.getCompletionStatus() == null) {
            task.setCompletionStatus("pending");
        }
        if (task.getCurrentStage() == null) {
            task.setCurrentStage("未开始");
        }
        if (task.getPriority() == null) {
            task.setPriority("medium");
        }
        return this.save(task);
    }

    @Override
    public List<Task> getTasksByInitiator(Long initiatorId) {
        return this.baseMapper.getTasksByInitiatorId(initiatorId);
    }

    @Override
    public List<Task> getTasksByStatus(String completionStatus) {
        return this.baseMapper.getTasksByCompletionStatus(completionStatus);
    }

    @Override
    public boolean updateTaskStatus(Long taskId, String status) {
        Task task = new Task();
        task.setId(taskId);
        task.setCompletionStatus(status);
        return this.updateById(task);
    }

    @Override
    public boolean updateCurrentStage(Long taskId, String stage) {
        Task task = new Task();
        task.setId(taskId);
        task.setCurrentStage(stage);
        return this.updateById(task);
    }

    @Override
    public boolean removeTask(Long taskId) {
        return this.removeById(taskId);
    }

    @Override
    public List<Task> getAllTasks() {
        // 使用自定义 Mapper 方法获取包含统计信息的任务列表
        return this.baseMapper.getAllTasks();
    }

    @Override
    public Task getTaskDetail(Long taskId) {
        QueryWrapper<Task> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", taskId);
        queryWrapper.eq("deleted", 0);
        return this.getOne(queryWrapper);
    }
}
