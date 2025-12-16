package top.myzo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.myzo.backend.entity.Permission;
import top.myzo.backend.service.PermissionService;
import top.myzo.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissonController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        Permission p = permissionService.getById(id);
        return p != null ? Result.success(p) : Result.error("权限不存在");
    }

    @PostMapping
    public Result create(@RequestBody Permission permission) {
        return permissionService.save(permission) ? Result.success("创建成功", permission) : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Permission permission) {
        permission.setId(id);
        return permissionService.saveOrUpdate(permission) ? Result.success("更新成功", permission) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result remove(@PathVariable Long id) {
        return permissionService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}
