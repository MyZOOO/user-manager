package top.myzo.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.myzo.backend.entity.LogInfo;
import top.myzo.backend.service.LogInfoService;
import top.myzo.backend.utils.Result;
import top.myzo.backend.utils.Status;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/loginfo")
public class LogInfoController {

    @Autowired
    private LogInfoService logInfoService;

    @GetMapping("/osinfo")
    public String getOsInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        return Status.parseOS(userAgent);
    }

    @GetMapping("/list")
    @RequiresRoles("admin")
    public Result list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer size,
                       @RequestParam(required = false) String username,
                       @RequestParam(required = false) String status) {
        Page<LogInfo> pageInfo = new Page<>(page, size);
        return Result.success(logInfoService.getLogList(pageInfo, username, status));
    }

}
