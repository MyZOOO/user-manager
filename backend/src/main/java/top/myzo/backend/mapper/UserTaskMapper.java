package top.myzo.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.myzo.backend.entity.UserTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserTaskMapper extends BaseMapper<UserTask> {
    /**
     * 根据用户ID查询分配给该用户的任务
     */
    List<UserTask> getTasksByUserId(Long userId);

    /**
     * 根据任务ID查询该任务分配给哪些用户
     */
    List<UserTask> getUsersByTaskId(Long taskId);

    /**
     * 根据用户ID和任务ID查询
     */
    UserTask getUserTask(Long userId, Long taskId);
}
