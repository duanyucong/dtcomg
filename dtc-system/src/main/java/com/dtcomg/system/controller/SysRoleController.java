package com.dtcomg.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dtcomg.system.common.Result;
import com.dtcomg.system.common.PageResult;
import com.dtcomg.system.domain.SysRole;
import com.dtcomg.system.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "角色管理")
@RestController
@RequestMapping("/api/roles")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    @Operation(summary = "获取角色分页列表")
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    public Result<PageResult<SysRole>> list(SysRole role, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int pageSize) {
        Page<SysRole> rolePage = new Page<>(page, pageSize);
        roleService.list(rolePage, role);

        PageResult<SysRole> pageResult = new PageResult<>(rolePage.getRecords(), rolePage.getTotal(), rolePage.getCurrent(), rolePage.getSize());
        return Result.success(pageResult);
    }

    @Operation(summary = "根据ID获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    public Result<SysRole> getRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(roleService.getById(id));
    }

    @Operation(summary = "新增角色")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    public Result<?> addRole(@RequestBody SysRole role) {
        return Result.success(roleService.save(role));
    }

    @Operation(summary = "修改角色信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public Result<?> editRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @RequestBody SysRole role) {
        role.setRoleId(id);
        return Result.success(roleService.updateById(role));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    public Result<?> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(roleService.deleteRole(id));
    }

    @Operation(summary = "获取角色的菜单ID列表")
    @GetMapping("/{id}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    public Result<List<Long>> getRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id) {
        return Result.success(roleService.getRoleMenuIds(id));
    }

    @Operation(summary = "更新角色菜单")
    @PutMapping("/{id}/menus")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    public Result<?> updateRoleMenus(@Parameter(description = "角色ID") @PathVariable Long id, @RequestBody List<Long> menuIds) {
        roleService.updateRoleMenus(id, menuIds);
        return Result.success();
    }
}
