package com.dtcomg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtcomg.system.domain.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 角色菜单关联 Mapper
 */
@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    /**
     * 批量新增角色菜单信息
     *
     * @param roleMenuList 角色菜单列表
     * @return 结果
     */
    int batchInsert(List<SysRoleMenu> roleMenuList);
}
