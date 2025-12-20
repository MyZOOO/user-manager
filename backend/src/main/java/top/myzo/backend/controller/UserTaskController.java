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

import java.util.List;

@RestController
@RequestMapping("/user-task")
public class UserTaskController {

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 管理员分配任务给用户
     */
    @RequiresRoles("admin")
    @PostMapping("/assign")
    public Result assignTask(@RequestParam Long taskId, @RequestParam Long userId) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        // 检查是否已经分配
        UserTask existing = userTaskService.getUserTaskRecord(userId, taskId);
        if (existing != null) {
            return Result.error("该任务已分配给此用户");
        }

        UserTask userTask = new UserTask();
        userTask.setTaskId(taskId);
        userTask.setTaskName(task.getTaskName());
        userTask.setUserId(userId);
        userTask.setUsername(user.getUsername());
        userTask.setAssignStatus("assigned");
        userTask.setCompletionStatus("pending");

        boolean result = userTaskService.save(userTask);
        if (result) {
            return Result.success("任务分配成功", userTask);
        }
        return Result.error("任务分配失败");
    }

    /**
     * 管理员批量分配任务给多个用户
     */
    @RequiresRoles("admin")
    @PostMapping("/batch-assign")
    public Result batchAssignTask(@RequestParam Long taskId, @RequestBody List<Long> userIds) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        if (userIds == null || userIds.isEmpty()) {
            return Result.error("用户列表不能为空");
        }

        int successCount = 0;
        for (Long userId : userIds) {
            User user = userService.getById(userId);
            if (user == null) {
                continue;
            }

            UserTask existing = userTaskService.getUserTaskRecord(userId, taskId);
            if (existing != null) {
                continue;
            }

            UserTask userTask = new UserTask();
            userTask.setTaskId(taskId);
            userTask.setTaskName(task.getTaskName());
            userTask.setUserId(userId);
            userTask.setUsername(user.getUsername());
            userTask.setAssignStatus("assigned");
            userTask.setCompletionStatus("pending");

            if (userTaskService.save(userTask)) {
                successCount++;
            }
        }

        return Result.success("批量分配完成，成功分配 " + successCount + " 个用户");
    }

    /**
     * 用户获取自己的任务列表
     */
    @RequiresAuthentication
    @GetMapping("/my-tasks")
    public Result getMyTasks() {
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

        List<UserTask> tasks = userTaskService.getUserTasks(currentUser.getId());
        return Result.success("获取成功", tasks);
    }

    /**
     * 获取任务的所有分配用户
     */
    @RequiresRoles("admin")
    @GetMapping("/task/{taskId}/assignees")
    public Result getTaskAssignees(@PathVariable Long taskId) {
        Task task = taskService.getTaskDetail(taskId);
        if (task == null) {
            return Result.error("任务不存在");
        }

        List<UserTask> assignees = userTaskService.getTaskAssignees(taskId);
        return Result.success("获取成功", assignees);
    }

    /**
     * 用户接受任务
     */
    @RequiresAuthentication
    @PutMapping("/{userTaskId}/accept")
    public Result acceptTask(@PathVariable Long userTaskId) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            return Result.error("任务分配记录不存在");
        }

        // 验证当前用户是否是该任务的分配对象
        Object principal = SecurityUtils.getSubject().getPrincipal();
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        String username = claims.getSubject();
        User currentUser = userService.getUserByUsername(username);

        if (!userTask.getUserId().equals(currentUser.getId())) {
            return Result.error("只能接受分配给自己的任务");
        }

        boolean result = userTaskService.acceptTask(userTaskId);
        if (result) {
            return Result.success("任务已接受");
        }
        return Result.error("接受任务失败");
    }

    /**
     * 用户拒绝任务
     */
    @RequiresAuthentication
    @PutMapping("/{userTaskId}/reject")
    public Result rejectTask(@PathVariable Long userTaskId, @RequestParam String reason) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            return Result.error("任务分配记录不存在");
        }

        // 验证当前用户
        Object principal = SecurityUtils.getSubject().getPrincipal();
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        String username = claims.getSubject();
        User currentUser = userService.getUserByUsername(username);

        if (!userTask.getUserId().equals(currentUser.getId())) {
            return Result.error("只能拒绝分配给自己的任务");
        }

        boolean result = userTaskService.rejectTask(userTaskId, reason);
        if (result) {
            return Result.success("任务已拒绝");
        }
        return Result.error("拒绝任务失败");
    }

    /**
     * 用户完成任务
     */
    @RequiresAuthentication
    @PutMapping("/{userTaskId}/complete")
    public Result completeTask(@PathVariable Long userTaskId, @RequestParam String result) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            return Result.error("任务分配记录不存在");
        }

        // 验证当前用户
        Object principal = SecurityUtils.getSubject().getPrincipal();
        String token = principal.toString();
        Claims claims = jwtUtil.getClaimByToken(token);
        String username = claims.getSubject();
        User currentUser = userService.getUserByUsername(username);

        if (!userTask.getUserId().equals(currentUser.getId())) {
            return Result.error("只能完成分配给自己的任务");
        }

        boolean completeResult = userTaskService.completeTask(userTaskId, result);
        if (completeResult) {
            return Result.success("任务已完成");
        }
        return Result.error("完成任务失败");
    }

    /**
     * 管理员取消任务分配
     */
    @RequiresRoles("admin")
    @DeleteMapping("/{userTaskId}")
    public Result removeTaskAssignment(@PathVariable Long userTaskId) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            return Result.error("任务分配记录不存在");
        }

        boolean result = userTaskService.removeTaskAssignment(userTaskId);
        if (result) {
            return Result.success("任务分配已取消");
        }
        return Result.error("取消分配失败");
    }

    /**
     * 获取用户任务的详细信息
     */
    @RequiresAuthentication
    @GetMapping("/{userTaskId}")
    public Result getUserTaskDetail(@PathVariable Long userTaskId) {
        UserTask userTask = userTaskService.getById(userTaskId);
        if (userTask == null) {
            return Result.error("任务分配记录不存在");
        }

        // 获取任务详情
        Task task = taskService.getTaskDetail(userTask.getTaskId());
        if (task == null) {
            return Result.error("任务不存在");
        }

        // 将任务信息和用户任务信息合并返回
        java.util.Map<String, Object> detail = new java.util.HashMap<>();
        detail.put("userTask", userTask);
        detail.put("task", task);

        return Result.success("获取成功", detail);
    }
}
