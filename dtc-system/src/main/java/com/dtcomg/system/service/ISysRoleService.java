package com.dtcomg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dtcomg.system.domain.SysRole;

import java.util.List;
import java.util.Set;

/**
 * Role Service interface.
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    Set<String> findRolesByUserId(Long userId);

    /**
     * 修改角色菜单
     * @param roleId 角色ID
     * @param menuIds 菜单ID列表
     */
    void updateRoleMenus(Long roleId, List<Long> menuIds);
}
