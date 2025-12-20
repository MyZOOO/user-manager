package top.myzo.backend.controller;

import top.myzo.backend.entity.Task;
import top.myzo.backend.entity.User;
import top.myzo.backend.entity.UserTask;
import top.myzo.backend.service.TaskService;
import top.myzo.backend.service.UserService;
import top.myzo.backend.service.UserTaskService;
import top.myzo.backend.utils.JwtUtil;
import top.myzo.backend.utils.Result;
import io.jsonwebtoken.Claims;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员发布任务
     * @RequiresRoles("admin") 只有管理员可以发布任务
     */
    @RequiresRoles("admin")
    @PostMapping("/publish")
    public Result publishTask(@RequestBody Task task) {
        System.out.println("\n========================================");
        System.out.println("【第1步】方法 publishTask 开始执行");
        System.out.println("【第1步】接收到的 task 数据：" + task);
        
        // 从 Shiro 的 Subject 中获取当前用户信息
        System.out.println("【第2步】从 Shiro 获取 principal");
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            System.out.println("【第2步】ERROR：principal 为 null，未登录");
            return Result.unauthorized("未登录");
        }
        System.out.println("【第2步】成功获取 principal");

        System.out.println("【第3步】验证 Token");
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null) {
            System.out.println("【第3步】ERROR：Token 无效");
            return Result.error("Token 无效");
        }
        System.out.println("【第3步】Token 有效");

        System.out.println("【第4步】获取当前用户信息");
        String username = claims.getSubject();
        System.out.println("【第4步】用户名：" + username);
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            System.out.println("【第4步】ERROR：获取用户信息失败");
            return Result.error("获取用户信息失败");
        }
        System.out.println("【第4步】成功获取用户：" + currentUser.getUsername() + "（ID=" + currentUser.getId() + "）");

        // 设置发起人信息
        System.out.println("【第5步】设置发起人信息");
        task.setInitiatorId(currentUser.getId());
        task.setInitiatorName(currentUser.getUsername());
        task.setPublishedTime(LocalDateTime.now());
        System.out.println("【第5步】发起人信息设置完成");

        System.out.println("【第6步】验证任务数据");
        if (task.getTaskName() == null || task.getTaskName().isEmpty()) {
            System.out.println("【第6步】ERROR：任务名称为空");
            return Result.error("任务名称不能为空");
        }
        System.out.println("【第6步】任务名称：" + task.getTaskName());

        if (task.getDeadline() == null) {
            System.out.println("【第6步】ERROR：截止时间为空");
            return Result.error("限定完成时间不能为空");
        }
        System.out.println("【第6步】截止时间：" + task.getDeadline());

        // 设置默认值
        System.out.println("【第7步】设置任务默认值");
        if (task.getCompletionStatus() == null) {
            task.setCompletionStatus("pending");
        }
        if (task.getCurrentStage() == null) {
            task.setCurrentStage("未开始");
        }
        if (task.getPriority() == null) {
            task.setPriority("medium");
        }
        System.out.println("【第7步】默认值设置完成");

        System.out.println("【第8步】调用 taskService.publishTask(task) 保存任务");
        boolean result = taskService.publishTask(task);
        System.out.println("【第8步】publishTask 返回结果：" + result);
        
        if (result) {
            System.out.println("【第9步】任务保存成功");
            System.out.println("【第9步】保存后 task 对象数据：id=" + task.getId() + ", name=" + task.getTaskName());
            System.out.println("========================================\n");
            return Result.success("任务发布成功", task);
        } else {
            System.out.println("【第8步】ERROR：publishTask 返回 false，任务保存失败");
            System.out.println("========================================\n");
            return Result.error("任务发布失败");
        }
    }

    /**
     * 获取管理员发布的所有任务
     */
    @RequiresRoles("admin")
    @GetMapping("/my-published")
    public Result getMyPublishedTasks() {
        Object principal = SecurityUtils.getSubject().getPrincipal();
        if (principal == null) {
            return Result.unauthorized("未登录");
        }

        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        if (claims == null) {
            return Result.error("Token 无效");
        }

        String username = claims.getSubject();
        User currentUser = userService.getUserByUsername(username);
        if (currentUser == null) {
            return Result.error("获取用户信息失败");
        }

        List<Task> tasks = taskService.getTasksByInitiator(currentUser.getId());
        return Result.success("获取成功", tasks);
    }

    /**
     * 获取所有任务列表
     */
    @RequiresAuthentication
    @GetMapping("/list")
    public Result getTaskList() {
        List<Task> tasks = taskService.getAllTasks();
        return Result.success("获取成功", tasks);
    }

    /**
     * 获取任务详情
     */
    @RequiresAuthentication
    @GetMapping("/{taskId}")
    public Result getTaskDetail(@PathVariable Long taskId) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }
        return Result.success("获取成功", task);
    }

    /**
     * 根据完成状态查询任务
     */
    @RequiresAuthentication
    @GetMapping("/status/{status}")
    public Result getTasksByStatus(@PathVariable String status) {
        List<Task> tasks = taskService.getTasksByStatus(status);
        return Result.success("获取成功", tasks);
    }

    /**
     * 管理员更新任务状态
     */
    @RequiresRoles("admin")
    @PutMapping("/{taskId}/status")
    public Result updateTaskStatus(@PathVariable Long taskId, @RequestParam String status) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        boolean result = taskService.updateTaskStatus(taskId, status);
        if (result) {
            return Result.success("状态更新成功");
        }
        return Result.error("状态更新失败");
    }

    /**
     * 管理员更新当前环节
     */
    @RequiresRoles("admin")
    @PutMapping("/{taskId}/stage")
    public Result updateCurrentStage(@PathVariable Long taskId, @RequestParam String stage) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        boolean result = taskService.updateCurrentStage(taskId, stage);
        if (result) {
            return Result.success("环节更新成功");
        }
        return Result.error("环节更新失败");
    }

    /**
     * 管理员编辑任务
     */
    @RequiresRoles("admin")
    @PutMapping("/{taskId}")
    public Result updateTask(@PathVariable Long taskId, @RequestBody Task task) {
        Task existingTask = taskService.getTaskDetail(taskId);
        if (existingTask == null) {
            return Result.error("任务不存在");
        }

        task.setId(taskId);
        boolean result = taskService.updateById(task);
        if (result) {
            return Result.success("任务更新成功", task);
        }
        return Result.error("任务更新失败");
    }

    /**
     * 管理员删除任务
     */
    @RequiresRoles("admin")
    @DeleteMapping("/{taskId}")
    public Result deleteTask(@PathVariable Long taskId) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        boolean result = taskService.removeTask(taskId);
        if (result) {
            return Result.success("任务删除成功");
        }
        return Result.error("任务删除失败");
    }
}
