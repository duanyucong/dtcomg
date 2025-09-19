package com.dtcomg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.common.PageResult;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Operation(summary = "获取用户分页列表")
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public ApiResult<PageResult<SysUser>> list(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") int pageSize) {

        Page<SysUser> userPage = new Page<>(page, pageSize);
        userService.page(userPage);

        PageResult<SysUser> pageResult = new PageResult<>(userPage.getRecords(), userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
        return ApiResult.success(pageResult);
    }

    @Operation(summary = "根据ID获取用户详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public ApiResult<SysUser> getUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return ApiResult.success(userService.getById(id));
    }

    @Operation(summary = "新增用户")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    public ApiResult<?> addUser(@RequestBody SysUser user) {
        // In a real app, hash the password before saving
        return ApiResult.success(userService.save(user));
    }

    @Operation(summary = "修改用户信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public ApiResult<?> editUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @RequestBody SysUser user) {
        user.setUserId(id);
        return ApiResult.success(userService.updateById(user));
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    public ApiResult<?> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        return ApiResult.success(userService.removeById(id));
    }
}
