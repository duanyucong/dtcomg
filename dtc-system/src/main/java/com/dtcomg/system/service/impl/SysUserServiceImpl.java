package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.mapper.SysUserMapper;
import com.dtcomg.system.service.ISysUserService;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.dtcomg.system.domain.SysUserRole;
import com.dtcomg.system.mapper.SysUserRoleMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

/**
 * User Service implementation.
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {


    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Override
    public boolean createUser(SysUser user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return save(user);
    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // 1. Delete old roles
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));

        // 2. Add new roles
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = roleIds.stream().map(roleId -> {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                return userRole;
            }).collect(Collectors.toList());
            userRoleMapper.batchInsert(userRoles);
        }
    }
}
