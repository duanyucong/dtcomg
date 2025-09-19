package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.mapper.SysUserMapper;
import com.dtcomg.system.service.ISysUserService;
import org.springframework.stereotype.Service;

/**
 * User Service implementation.
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {
}
