package top.myzo.backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.myzo.backend.entity.Menu;
import top.myzo.backend.service.RelationService;
import top.myzo.backend.utils.Result;

import java.util.List;

@RestController
@RequestMapping("/relations")
public class RelationController {

    @Autowired
    private RelationService relationService;

    // 用户-角色
    @PostMapping("/users/{userId}/roles:bind")
    public Result bindUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return relationService.bindUserRoles(userId, roleIds) ? Result.success("绑定成功") : Result.error("绑定失败");
    }

    @PostMapping("/users/{userId}/roles:unbind")
    public Result unbindUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return relationService.unbindUserRoles(userId, roleIds) ? Result.success("解绑成功") : Result.error("解绑失败");
    }

    @PutMapping("/users/{userId}/roles")
    public Result replaceUserRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        return relationService.replaceUserRoles(userId, roleIds) ? Result.success("替换成功") : Result.error("替换失败");
    }

    // 角色-权限
    @PostMapping("/roles/{roleId}/permissions:bind")
    public Result bindRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return relationService.bindRolePermissions(roleId, permissionIds) ? Result.success("绑定成功") : Result.error("绑定失败");
    }

    @PostMapping("/roles/{roleId}/permissions:unbind")
    public Result unbindRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return relationService.unbindRolePermissions(roleId, permissionIds) ? Result.success("解绑成功") : Result.error("解绑失败");
    }

    @PutMapping("/roles/{roleId}/permissions")
    public Result replaceRolePermissions(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        return relationService.replaceRolePermissions(roleId, permissionIds) ? Result.success("替换成功") : Result.error("替换失败");
    }

    // 角色-菜单
    @PostMapping("/roles/{roleId}/menus:bind")
    public Result bindRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        return relationService.bindRoleMenus(roleId, menuIds) ? Result.success("绑定成功") : Result.error("绑定失败");
    }

    @PostMapping("/roles/{roleId}/menus:unbind")
    public Result unbindRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        return relationService.unbindRoleMenus(roleId, menuIds) ? Result.success("解绑成功") : Result.error("解绑失败");
    }

    @PutMapping("/roles/{roleId}/menus")
    public Result replaceRoleMenus(@PathVariable Long roleId, @RequestBody List<Long> menuIds) {
        return relationService.replaceRoleMenus(roleId, menuIds) ? Result.success("替换成功") : Result.error("替换失败");
    }

    // 查询接口
    @GetMapping("/users/{userId}/roles:codes")
    public Result getUserRoleCodes(@PathVariable Long userId) {
        List<String> codes = relationService.getRoleCodesByUserId(userId);
        return Result.success(codes);
    }

    @GetMapping("/users/{userId}/permissions:codes")
    public Result getUserPermissionCodes(@PathVariable Long userId) {
        List<String> codes = relationService.getPermissionCodesByUserId(userId);
        return Result.success(codes);
    }

    @GetMapping("/users/{userId}/menus")
    public Result getUserMenus(@PathVariable Long userId) {
        List<Menu> menus = relationService.getMenusByUserId(userId);
        return Result.success(menus);
    }

    @GetMapping("/users/{userId}/menus:tree")
    public Result getUserMenuTree(@PathVariable Long userId) {
        List<Menu> menus = relationService.getMenuTreeByUserId(userId);
        return Result.success(menus);
    }
}
