package com.dtcomg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dtcomg.system.domain.SysMenu;

import java.util.List;
import java.util.Set;

/**
 * Menu Service interface.
 */
public interface ISysMenuService extends IService<SysMenu> {

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> findPermsByUserId(Long userId);

    /**
     * 构建导航菜单树
     * @param list 菜单列表
     * @return 树形结构
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    List<SysMenu> buildNevMenuTree(List<SysMenu> menus);

    List<SysMenu> selectMenuTreeByUserId(Long userId);

    /**
     * Check if a menu has child menus.
     *

    List<SysMenu> buildMenuTree(List<SysMenu> menus);

//    /**
//     * 获取所有正常状态的菜单树（用于导航菜单）
//     */
//    List<SysMenu> listActiveMenus();

    /**
     * 是否存在子节点
     * @param menuId 菜单ID
     * @return 结果
     */
    boolean hasChild(Long menuId);

    /**
     * 删除菜单
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);
}
