package com.dtcomg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.common.PageResult;
import com.dtcomg.system.domain.SysRole;
import com.dtcomg.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public ApiResult<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {

        Page<SysRole> rolePage = new Page<>(page, pageSize);
        roleService.page(rolePage);

        PageResult<SysRole> pageResult = new PageResult<>(rolePage.getRecords(), rolePage.getTotal(), rolePage.getCurrent(), rolePage.getSize());
        return ApiResult.success(pageResult);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    public ApiResult<SysRole> getRole(@PathVariable Long id) {
        return ApiResult.success(roleService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    public ApiResult<?> addRole(@RequestBody SysRole role) {
        return ApiResult.success(roleService.save(role));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public ApiResult<?> editRole(@PathVariable Long id, @RequestBody SysRole role) {
        role.setRoleId(id);
        return ApiResult.success(roleService.updateById(role));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    public ApiResult<?> deleteRole(@PathVariable Long id) {
        return ApiResult.success(roleService.removeById(id));
    }
}
