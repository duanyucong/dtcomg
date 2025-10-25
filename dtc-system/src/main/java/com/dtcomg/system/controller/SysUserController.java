package com.dtcomg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtcomg.system.common.Result;
import com.dtcomg.system.common.PageResult;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.dto.ResetPasswordDTO;
import com.dtcomg.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Operation(summary = "获取用户分页列表")
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public Result<PageResult<SysUser>> list(SysUser user, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysUser> userPage = new Page<>(page, pageSize);
        Page<SysUser> pageResult = userService.list(userPage, user);

        PageResult<SysUser> result = new PageResult<>(pageResult.getRecords(), pageResult.getTotal(), pageResult.getCurrent(), pageResult.getSize());
        return Result.success(result);
    }

    @Operation(summary = "根据ID获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public Result<SysUser> getUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    public Result<?> addUser(@RequestBody SysUser user) {
        return Result.success(userService.createUser(user));
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public Result<?> editUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody SysUser user) {
        user.setUserId(id);
        return Result.success(userService.updateById(user));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    public Result<?> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return Result.success(userService.deleteUser(id));
    }

    @Operation(summary = "更新用户角色")
    @PutMapping("/{id}/roles")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public Result<?> updateUserRoles(@Parameter(description = "用户ID") @PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.updateUserRoles(id, roleIds);
        return Result.success();
    }

    @Operation(summary = "重置用户密码")
    @PutMapping("/{id}/password")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public Result<?> resetPassword(@Parameter(description = "用户ID") @PathVariable Long id, @RequestBody ResetPasswordDTO resetPasswordDTO) {
        log.info("接收到重置密码请求，用户ID: {}，新密码长度: {}", id, 
                resetPasswordDTO.getNewPassword() != null ? resetPasswordDTO.getNewPassword().length() : 0);
        userService.resetUserPassword(id, resetPasswordDTO.getNewPassword());
        log.info("重置密码请求处理完成，用户ID: {}", id);
        return Result.success();
    }
}
