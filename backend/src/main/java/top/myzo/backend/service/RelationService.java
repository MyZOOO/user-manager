package top.myzo.backend.service;

import top.myzo.backend.entity.Menu;

import java.util.List;

public interface RelationService {
    // 用户-角色绑定
    boolean bindUserRoles(Long userId, List<Long> roleIds);
    boolean unbindUserRoles(Long userId, List<Long> roleIds);
    boolean replaceUserRoles(Long userId, List<Long> roleIds);

    // 角色-权限绑定
    boolean bindRolePermissions(Long roleId, List<Long> permissionIds);
    boolean unbindRolePermissions(Long roleId, List<Long> permissionIds);
    boolean replaceRolePermissions(Long roleId, List<Long> permissionIds);

    // 角色-菜单绑定
    boolean bindRoleMenus(Long roleId, List<Long> menuIds);
    boolean unbindRoleMenus(Long roleId, List<Long> menuIds);
    boolean replaceRoleMenus(Long roleId, List<Long> menuIds);

    // 用户关联查询
    List<String> getRoleCodesByUserId(Long userId);
    List<String> getPermissionCodesByUserId(Long userId);
    List<Menu> getMenusByUserId(Long userId);
    List<Menu> getMenuTreeByUserId(Long userId);
}
