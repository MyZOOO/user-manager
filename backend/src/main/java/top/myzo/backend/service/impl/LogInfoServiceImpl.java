package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import top.myzo.backend.entity.LogInfo;
import top.myzo.backend.mapper.LogInfoMapper;
import top.myzo.backend.service.LogInfoService;

@Service
public class LogInfoServiceImpl extends ServiceImpl<LogInfoMapper, LogInfo> implements LogInfoService {

    @Override
    public Page<LogInfo> getLogList(Page<LogInfo> page, String username, String status) {
        QueryWrapper<LogInfo> queryWrapper = new QueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            queryWrapper.like("username", username);
        }
        if (status != null && !status.isEmpty()) {
            queryWrapper.like("status", status);
        }
        
        // 按创建时间倒序排列
        queryWrapper.orderByDesc("created_time");
        
        return this.page(page, queryWrapper);
    }
}
