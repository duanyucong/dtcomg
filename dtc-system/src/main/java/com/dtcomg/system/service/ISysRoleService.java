package com.dtcomg.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dtcomg.system.domain.SysRole;

import java.util.List;
import java.util.Set;

/**
 * Role Service interface.
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     *
     * @param page 分页对象
     * @param role 查询条件
     * @return 分页结果
     */
    Page<SysRole> list(Page<SysRole> page, SysRole role);

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    Set<String> findRolesByUserId(Long userId);

    /**
     * 根据用户名查询角色
     *
     * @param username 用户名
     * @return 角色列表
     */
    Set<String> findRolesByUserName(String username);

    /**
     * 修改角色菜单
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void updateRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 根据角色ID查询菜单ID列表
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 获取管理员角色ID
     * @return 管理员角色ID
     */
    Long getAdminRoleId();

    /**
     * 删除角色（逻辑删除）
     * @param roleId 角色ID
     * @return 结果
     */
    boolean deleteRole(Long roleId);
}
