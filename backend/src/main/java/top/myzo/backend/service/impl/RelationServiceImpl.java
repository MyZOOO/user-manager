package top.myzo.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import top.myzo.backend.entity.*;
import top.myzo.backend.mapper.*;
import top.myzo.backend.service.MenuService;
import top.myzo.backend.service.RelationService;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RelationServiceImpl implements RelationService {

    @Resource private UserRoleMapper userRoleMapper;
    @Resource private RolePermissionMapper rolePermissionMapper;
    @Resource private RoleMenuMapper roleMenuMapper;
    @Resource private RoleMapper roleMapper;
    @Resource private PermissionMapper permissionMapper;
    @Resource private MenuMapper menuMapper;
    @Resource private MenuService menuService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null) return false;
        for (Long rid : roleIds) {
            UserRole ur = new UserRole();
            ur.setUserId(userId);
            ur.setRoleId(rid);
            userRoleMapper.insert(ur);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null || roleIds == null) return false;
        for (Long rid : roleIds) {
            userRoleMapper.delete(new LambdaQueryWrapper<UserRole>()
                    .eq(UserRole::getUserId, userId)
                    .eq(UserRole::getRoleId, rid));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replaceUserRoles(Long userId, List<Long> roleIds) {
        if (userId == null) return false;
        userRoleMapper.delete(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (roleIds != null && !roleIds.isEmpty()) {
            bindUserRoles(userId, roleIds);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRolePermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null || permissionIds == null) return false;
        for (Long pid : permissionIds) {
            RolePermission rp = new RolePermission();
            rp.setRoleId(roleId);
            rp.setPermissionId(pid);
            rolePermissionMapper.insert(rp);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindRolePermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null || permissionIds == null) return false;
        for (Long pid : permissionIds) {
            rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>()
                    .eq(RolePermission::getRoleId, roleId)
                    .eq(RolePermission::getPermissionId, pid));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replaceRolePermissions(Long roleId, List<Long> permissionIds) {
        if (roleId == null) return false;
        rolePermissionMapper.delete(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId));
        if (permissionIds != null && !permissionIds.isEmpty()) {
            bindRolePermissions(roleId, permissionIds);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean bindRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null || menuIds == null) return false;
        for (Long mid : menuIds) {
            RoleMenu rm = new RoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(mid);
            roleMenuMapper.insert(rm);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean unbindRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null || menuIds == null) return false;
        for (Long mid : menuIds) {
            roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>()
                    .eq(RoleMenu::getRoleId, roleId)
                    .eq(RoleMenu::getMenuId, mid));
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean replaceRoleMenus(Long roleId, List<Long> menuIds) {
        if (roleId == null) return false;
        roleMenuMapper.delete(new LambdaQueryWrapper<RoleMenu>().eq(RoleMenu::getRoleId, roleId));
        if (menuIds != null && !menuIds.isEmpty()) {
            bindRoleMenus(roleId, menuIds);
        }
        return true;
    }

    @Override
    public List<String> getRoleCodesByUserId(Long userId) {
        List<UserRole> urs = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) return Collections.emptyList();
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();
        List<Role> roles = roleMapper.selectBatchIds(roleIds);
        return roles.stream().map(Role::getCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @Override
    public List<String> getPermissionCodesByUserId(Long userId) {
        // user -> roles -> permissions
        List<UserRole> urs = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) return Collections.emptyList();
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();
        List<RolePermission> rps = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds));
        if (rps.isEmpty()) return Collections.emptyList();
        List<Long> pids = rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toList());
        if (pids.isEmpty()) return Collections.emptyList();
        List<Permission> ps = permissionMapper.selectBatchIds(pids);
        return ps.stream().map(Permission::getCode).filter(Objects::nonNull).distinct().collect(Collectors.toList());
    }

    @Override
    public List<Menu> getMenusByUserId(Long userId) {
        List<UserRole> urs = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        if (urs.isEmpty()) return Collections.emptyList();
        List<Long> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return Collections.emptyList();
        List<RoleMenu> rms = roleMenuMapper.selectList(new LambdaQueryWrapper<RoleMenu>().in(RoleMenu::getRoleId, roleIds));
        if (rms.isEmpty()) return Collections.emptyList();
        List<Long> menuIds = rms.stream().map(RoleMenu::getMenuId).collect(Collectors.toList());
        if (menuIds.isEmpty()) return Collections.emptyList();
        return menuMapper.selectBatchIds(menuIds);
    }

    @Override
    public List<Menu> getMenuTreeByUserId(Long userId) {
        List<Menu> menus = getMenusByUserId(userId);
        if (menus.isEmpty()) return Collections.emptyList();
        return menuService.buildMenuTree(menus);
    }
}
