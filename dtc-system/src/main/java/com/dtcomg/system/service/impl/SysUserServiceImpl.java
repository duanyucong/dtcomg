package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.domain.SysUserRole;
import com.dtcomg.system.mapper.SysUserMapper;
import com.dtcomg.system.mapper.SysUserRoleMapper;
import com.dtcomg.system.service.ISysRoleService;
import com.dtcomg.system.service.ISysUserService;
import com.dtcomg.system.common.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service implementation.
 */
@Slf4j
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements ISysUserService {


    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private ISysRoleService roleService; // Inject ISysRoleService

    private boolean isUserAdmin(Long userId) {
        if (roleService.getAdminRoleId() == null) {
            return false; // Admin role not found, cannot determine if user is admin
        }
        List<SysUserRole> userRoles = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        return userRoles.stream().anyMatch(userRole -> userRole.getRoleId().equals(roleService.getAdminRoleId()));

    }
    @Override
    @Transactional
    public boolean createUser(SysUser user) {
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 保存用户基本信息
        boolean result = save(user);
        
        if (result) {
            // 如果创建成功
            if (user.getRoleIds() != null && !user.getRoleIds().isEmpty()) {
                // 如果前端传入了角色列表，使用前端传入的角色
                updateUserRoles(user.getUserId(), user.getRoleIds());
            } else {
                // 如果前端没有传入角色，可以在这里设置默认角色
                // 例如：分配普通用户角色（需要根据实际情况设置角色ID）
                // List<Long> defaultRoles = Arrays.asList(2L); // 假设2是普通用户角色ID
                // updateUserRoles(user.getUserId(), defaultRoles);
            }
        }
        
        return result;
    }

    @Override
    public boolean updateById(SysUser entity) {
        // Check if the user being updated is an admin
        if (isUserAdmin(entity.getUserId())) {
            throw new ServiceException("不允许修改管理员用户信息");
        }
        return super.updateById(entity);
    }

    @Override
    @Transactional
    public void updateUserRoles(Long userId, List<Long> roleIds) {
        // Check if the user is an admin
        if (isUserAdmin(userId)) {
            throw new ServiceException("不允许修改管理员的角色");
        }
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

    @Override
    public Page<SysUser> list(Page<SysUser> page, SysUser user) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(user.getUserName())) {
            wrapper.like(SysUser::getUserName, user.getUserName());
        }
        if (StringUtils.isNotBlank(user.getStatus())) {
            wrapper.eq(SysUser::getStatus, user.getStatus());
        }
        if (StringUtils.isNotBlank(user.getPhoneNumber())) {
            wrapper.like(SysUser::getPhoneNumber, user.getPhoneNumber());
        }
        if (user.getDeptId() != null && user.getDeptId() != 0) {
            wrapper.eq(SysUser::getDeptId, user.getDeptId());
        }
        Page<SysUser> userPage = baseMapper.selectPage(page, wrapper);

        // Populate roleIds for each user
        userPage.getRecords().forEach(sysUser -> {
            List<Long> roleIds = userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                            .eq(SysUserRole::getUserId, sysUser.getUserId()))
                    .stream()
                    .map(SysUserRole::getRoleId)
                    .collect(Collectors.toList());
            sysUser.setRoleIds(roleIds);
        });

        return userPage;
    }

    @Override
    @Transactional
    public boolean resetUserPassword(Long userId, String newPassword) {
        log.info("开始重置用户密码，用户ID: {}", userId);
        
        // Check if the user is an admin
        if (isUserAdmin(userId)) {
            log.warn("尝试重置管理员用户密码，用户ID: {}", userId);
            throw new ServiceException("不允许重置管理员用户密码");
        }
        
        SysUser user = getById(userId);
        if (user == null) {
            log.warn("重置密码时用户不存在，用户ID: {}", userId);
            throw new ServiceException("用户不存在");
        }
        
        // 记录原始密码长度（不记录实际密码）
        log.info("正在重置用户密码，用户ID: {}，新密码长度: {}", userId, newPassword != null ? newPassword.length() : 0);
        
        String encodedPassword = passwordEncoder.encode(newPassword);
        log.info("密码加密完成，用户ID: {}，加密后密码长度: {}", userId, encodedPassword.length());
        
        user.setPassword(encodedPassword);
        boolean result = updateById(user);
        
        log.info("用户密码重置完成，用户ID: {}，结果: {}", userId, result);
        return result;
    }

    @Override
    @Transactional
    public boolean deleteUser(Long userId) {
        // Check if the user is an admin
        if (isUserAdmin(userId)) {
            log.warn("尝试删除管理员用户，用户ID: {}", userId);
            throw new ServiceException("不允许删除管理员用户");
        }
        
        // 检查用户是否已分配角色
        if (userRoleMapper.selectCount(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId)) > 0) {
            throw new ServiceException("用户已分配角色,不允许删除");
        }
        
        // 逻辑删除，使用LambdaUpdateWrapper直接更新del_flag为'2'
        LambdaUpdateWrapper<SysUser> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysUser::getUserId, userId).set(SysUser::getDelFlag, "2");
        return update(updateWrapper);
    }
}