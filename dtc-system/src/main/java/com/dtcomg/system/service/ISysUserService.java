package com.dtcomg.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dtcomg.system.domain.SysUser;

/**
 * User Service interface.
 */
import java.util.List;

public interface ISysUserService extends IService<SysUser> {
    /**
     * 创建用户
     * @param user 用户信息
     * @return 结果
     */
    boolean createUser(SysUser user);

    /**
     * 修改用户角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void updateUserRoles(Long userId, List<Long> roleIds);

    /**
     * 分页查询用户列表
     *
     * @param page 分页对象
     * @param user 查询条件
     * @return 分页结果
     */
    Page<SysUser> list(Page<SysUser> page, SysUser user);

    /**
     * 重置用户密码
     * @param userId 用户ID
     * @param newPassword 新密码
     * @return 结果
     */
    boolean resetUserPassword(Long userId, String newPassword);

    /**
     * 删除用户（逻辑删除）
     * @param userId 用户ID
     * @return 结果
     */
    boolean deleteUser(Long userId);
}
