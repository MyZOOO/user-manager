package top.myzo.backend.service;

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

    // 用户关联查询
    List<String> getRoleCodesByUserId(Long userId);
    List<String> getPermissionCodesByUserId(Long userId);
}
