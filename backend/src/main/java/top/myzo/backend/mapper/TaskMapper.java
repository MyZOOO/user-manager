package top.myzo.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.myzo.backend.entity.Task;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TaskMapper extends BaseMapper<Task> {
    /**
     * 根据发起人ID查询任务列表
     */
    List<Task> getTasksByInitiatorId(Long initiatorId);

    /**
     * 根据完成状态查询任务列表
     */
    List<Task> getTasksByCompletionStatus(String completionStatus);

    /**
     * 获取所有任务（包含统计信息）
     */
    List<Task> getAllTasks();
}
