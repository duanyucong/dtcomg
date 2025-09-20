package com.dtcomg.system.service;

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
}
