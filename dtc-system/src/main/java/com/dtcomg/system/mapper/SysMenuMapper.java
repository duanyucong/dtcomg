package com.dtcomg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtcomg.system.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * Menu Mapper interface.
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    Set<String> findPermsByUserId(Long userId);

    List<SysMenu> selectMenusByUserId(Long userId);
}
