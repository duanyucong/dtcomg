package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.common.ServiceException;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.domain.SysRoleMenu;
import com.dtcomg.system.mapper.SysMenuMapper;
import com.dtcomg.system.mapper.SysRoleMenuMapper;
import com.dtcomg.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Menu Service implementation.
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {

    @Autowired
    private SysRoleMenuMapper roleMenuMapper;

    @Override
    public Set<String> findPermsByUserId(Long userId) {
        return baseMapper.findPermsByUserId(userId);
    }


    @Override
    public List<SysMenu> list() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getOrderNum);
        return baseMapper.selectList(wrapper);
    }

//    @Override
//    public List<SysMenu> listActiveMenus() {
//        // 获取所有菜单并构建树形结构，同时过滤掉停用状态的菜单
//        return buildMenuTreeWithFilter(list());
//    }

    /**
     * 递归列表并过滤停用状态的菜单
     */
    private void recursionFnWithFilter(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表，只包含正常状态的子菜单
        List<SysMenu> childList = getChildListWithFilter(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFnWithFilter(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表，只包含正常状态的子菜单
     */
    private List<SysMenu> getChildListWithFilter(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            // 只添加正常状态且父ID匹配的子菜单
            if (n.getParentId().longValue() == t.getMenuId().longValue() && "0".equals(n.getStatus())) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).toList();

        for (SysMenu menu : menus) {
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(menu.getParentId())) {
                recursionFn(menus, menu);
                returnList.add(menu);
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus;
        }
        return returnList;
    }

    /**
     * 构建菜单树并过滤停用状态的菜单
     */
    public List<SysMenu> buildNevMenuTree(List<SysMenu> menus) {
        List<SysMenu> returnList = new ArrayList<>();
        List<Long> tempList = menus.stream().map(SysMenu::getMenuId).toList();

        for (SysMenu menu : menus) {
            // 只处理正常状态的菜单
            if ("0".equals(menu.getStatus())) {
                // 如果是顶级节点, 遍历该父节点的所有子节点
                if (!tempList.contains(menu.getParentId())) {
                    recursionFnWithFilter(menus, menu);
                    returnList.add(menu);
                }
            }
        }
        if (returnList.isEmpty()) {
            returnList = menus.stream()
                    .filter(menu -> "0".equals(menu.getStatus()))
                    .collect(Collectors.toList());
        }
        return returnList;
    }

    @Override
    public List<SysMenu> selectMenuTreeByUserId(Long userId) {
        List<SysMenu> menus = baseMapper.selectMenusByUserId(userId);
        return buildNevMenuTree(menus);
    }

    @Override
    public boolean hasChild(Long menuId) {
        return count(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, menuId)) > 0;
    }

    @Override
    public void deleteMenu(Long menuId) {
        // 检查是否存在子菜单
        if (hasChild(menuId)) {
            throw new ServiceException("存在子菜单,不允许删除");
        }
        
        // 检查菜单是否被分配给角色
        if (isMenuAssignedToRoles(menuId)) {
            throw new ServiceException("菜单已被分配给角色,不允许删除");
        }
        
        // 逻辑删除，使用LambdaUpdateWrapper直接更新del_flag为'2'
        LambdaUpdateWrapper<SysMenu> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysMenu::getMenuId, menuId).set(SysMenu::getDelFlag, "2");
        update(updateWrapper);
    }
    
    /**
     * 检查菜单是否被分配给角色
     * @param menuId 菜单ID
     * @return true-已被分配，false-未被分配
     */
    private boolean isMenuAssignedToRoles(Long menuId) {
        LambdaQueryWrapper<SysRoleMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenu::getMenuId, menuId);
        return roleMenuMapper.selectCount(wrapper) > 0;
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysMenu> list, SysMenu t) {
        // 得到子节点列表
        List<SysMenu> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysMenu tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysMenu> getChildList(List<SysMenu> list, SysMenu t) {
        List<SysMenu> tlist = new ArrayList<>();
        Iterator<SysMenu> it = list.iterator();
        while (it.hasNext()) {
            SysMenu n = it.next();
            if (n.getParentId().longValue() == t.getMenuId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysMenu> list, SysMenu t) {
        return !getChildList(list, t).isEmpty();
    }
}
