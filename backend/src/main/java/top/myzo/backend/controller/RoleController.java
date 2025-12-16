package top.myzo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.myzo.backend.entity.Role;
import top.myzo.backend.service.RoleService;
import top.myzo.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping
    public Result list() {
        List<Role> list = roleService.list();
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result get(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return role != null ? Result.success(role) : Result.error("角色不存在");
    }

    @PostMapping
    public Result create(@RequestBody Role role) {
        return roleService.save(role) ? Result.success("创建成功", role) : Result.error("创建失败");
    }

    @PutMapping("/{id}")
    public Result update(@PathVariable Long id, @RequestBody Role role) {
        role.setId(id);
        return roleService.saveOrUpdate(role) ? Result.success("更新成功", role) : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result remove(@PathVariable Long id) {
        return roleService.removeById(id) ? Result.success("删除成功") : Result.error("删除失败");
    }
}
