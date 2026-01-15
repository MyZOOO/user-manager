package top.myzo.backend.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import top.myzo.backend.entity.LogInfo;

public interface LogInfoService extends IService<LogInfo> {
    Page<LogInfo> getLogList(Page<LogInfo> page, String username, String status);
}
