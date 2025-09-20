package com.dtcomg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtcomg.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * Menu Mapper interface.
 */
@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据用户ID查询权限
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    Set<String> findPermsByUserId(@Param("userId") Long userId);
}
