package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.domain.SysRole;
import com.dtcomg.system.domain.SysRoleMenu;
import com.dtcomg.system.domain.SysUserRole;
import com.dtcomg.system.mapper.SysRoleMapper;
import com.dtcomg.system.mapper.SysRoleMenuMapper;
import com.dtcomg.system.mapper.SysUserRoleMapper;
import com.dtcomg.system.service.ISysMenuService;
import com.dtcomg.system.service.ISysRoleService;
import com.dtcomg.system.common.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Role Service implementation.
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper roleMapper; // Inject SysRoleMapper

    @Autowired
    private ISysMenuService menuService; // Inject ISysMenuService

    private Long adminRoleId = null;
    private Set<Long> systemManagementMenuIds = null;

    public Long getAdminRoleId() {
        if (adminRoleId == null) {
            SysRole adminRole = roleMapper.selectOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, "admin"));
            if (adminRole != null) {
                adminRoleId = adminRole.getRoleId();
            }
        }
        return adminRoleId;
    }

    private Set<Long> getSystemManagementMenuIds() {
        if (systemManagementMenuIds == null) {
            systemManagementMenuIds = new HashSet<>();
            SysMenu systemManagementRoot = menuService.getOne(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getMenuName, "系统管理").eq(SysMenu::getParentId, 0L));
            if (systemManagementRoot != null) {
                collectAllChildMenuIds(systemManagementRoot.getMenuId(), systemManagementMenuIds);
            }
        }
        return systemManagementMenuIds;
    }

    private void collectAllChildMenuIds(Long parentId, Set<Long> collectedIds) {
        collectedIds.add(parentId);
        List<SysMenu> children = menuService.list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, parentId));
        for (SysMenu child : children) {
            collectAllChildMenuIds(child.getMenuId(), collectedIds);
        }
    }

    @Override
    public Page<SysRole> list(Page<SysRole> page, SysRole role) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(role.getRoleName())) {
            wrapper.like(SysRole::getRoleName, role.getRoleName());
        }
        if (StringUtils.isNotBlank(role.getRoleKey())) {
            wrapper.like(SysRole::getRoleKey, role.getRoleKey());
        }
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateById(SysRole entity) {
        if (getAdminRoleId() != null && getAdminRoleId().equals(entity.getRoleId())) {
            throw new ServiceException("不允许修改管理员角色信息");
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
        if (getAdminRoleId() != null && getAdminRoleId().equals(roleId)) {
            // Check if any system management permission is being removed
            Set<Long> currentSystemManagementMenuIds = getSystemManagementMenuIds();
            if (currentSystemManagementMenuIds != null && !currentSystemManagementMenuIds.isEmpty()) {
                for (Long sysMenuId : currentSystemManagementMenuIds) {
                    if (!menuIds.contains(sysMenuId)) {
                        throw new ServiceException("超级管理员角色不允许取消系统管理中的任何权限");
                    }
                }
            }
            throw new ServiceException("不允许修改管理员角色的权限"); // General admin role permission modification restriction
        }
        // 1. Delete old menus
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId));

        // 2. Add new menus
        if (menuIds != null && !menuIds.isEmpty()) {
            List<SysRoleMenu> roleMenus = menuIds.stream().map(menuId -> {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                return roleMenu;
            }).collect(Collectors.toList());
            roleMenuMapper.batchInsert(roleMenus);
        }
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getRoleId, roleId);
        List<SysRoleMenu> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());
    }

    @Override
    public Set<String> findRolesByUserId(Long userId) {
        return baseMapper.findRolesByUserId(userId);
    }

    @Override
    public Set<String> findRolesByUserName(String username) {
        return baseMapper.findRolesByUserName(username);
    }

    @Override
    @Transactional
    public boolean deleteRole(Long roleId) {
        // Check if the role is admin role
        if (getAdminRoleId() != null && getAdminRoleId().equals(roleId)) {
            throw new ServiceException("不允许删除管理员角色");
        }
        
        // 检查角色是否已被分配给用户
        if (userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, roleId)) > 0) {
            throw new ServiceException("角色已分配给用户,不允许删除");
        }
        
        // 逻辑删除，使用LambdaUpdateWrapper直接更新del_flag为'2'
        LambdaUpdateWrapper<SysRole> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysRole::getRoleId, roleId).set(SysRole::getDelFlag, "2");
        return update(updateWrapper);
    }
}
