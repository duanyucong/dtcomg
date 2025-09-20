package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysRole;
import com.dtcomg.system.mapper.SysRoleMapper;
import com.dtcomg.system.service.ISysRoleService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtcomg.system.domain.SysRoleMenu;
import com.dtcomg.system.mapper.SysRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    public Set<String> findRolesByUserId(Long userId) {
        return baseMapper.findRolesByUserId(userId);
    }

    @Override
    @Transactional
    public void updateRoleMenus(Long roleId, List<Long> menuIds) {
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
}
