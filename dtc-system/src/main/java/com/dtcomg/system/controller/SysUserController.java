package com.dtcomg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.common.PageResult;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    public ApiResult<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<SysUser> userPage = new Page<>(page, pageSize);
        userService.page(userPage);

        PageResult<SysUser> pageResult = new PageResult<>(userPage.getRecords(), userPage.getTotal(), userPage.getCurrent(), userPage.getSize());
        return ApiResult.success(pageResult);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    public ApiResult<SysUser> getUser(@PathVariable Long id) {
        return ApiResult.success(userService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    public ApiResult<?> addUser(@RequestBody SysUser user) {
        // In a real app, hash the password before saving
        return ApiResult.success(userService.save(user));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    public ApiResult<?> editUser(@PathVariable Long id, @RequestBody SysUser user) {
        user.setUserId(id);
        return ApiResult.success(userService.updateById(user));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    public ApiResult<?> deleteUser(@PathVariable Long id) {
        return ApiResult.success(userService.removeById(id));
    }
}
