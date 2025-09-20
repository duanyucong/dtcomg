package com.dtcomg.system.domain;

import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户和角色关联 sys_user_role
 */
@TableName("sys_user_role")
public class SysUserRole {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }
}
