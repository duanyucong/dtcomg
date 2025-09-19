package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysRole;
import com.dtcomg.system.mapper.SysRoleMapper;
import com.dtcomg.system.service.ISysRoleService;
import org.springframework.stereotype.Service;

/**
 * Role Service implementation.
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements ISysRoleService {
}
