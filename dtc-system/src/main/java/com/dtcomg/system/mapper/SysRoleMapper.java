package com.dtcomg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtcomg.system.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Role Mapper interface.
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户ID查询角色
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    Set<String> findRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户名查询角色
     *
     * @param username 用户名
     * @return 角色列表
     */
    Set<String> findRolesByUserName(@Param("username") String username);
}
