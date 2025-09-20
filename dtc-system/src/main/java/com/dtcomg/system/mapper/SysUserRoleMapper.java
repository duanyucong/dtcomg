package com.dtcomg.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.dtcomg.system.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户角色关联 Mapper
 */
@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    /**
     * 批量新增用户角色信息
     *
     * @param userRoleList 用户角色列表
     * @return 结果
     */
    int batchInsert(List<SysUserRole> userRoleList);
}
