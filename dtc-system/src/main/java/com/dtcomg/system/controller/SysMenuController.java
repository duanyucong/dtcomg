package com.dtcomg.system.controller;

import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.service.ISysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "菜单管理")
@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    @Operation(summary = "获取菜单列表（树形）")
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public ApiResult<List<SysMenu>> list() {
        List<SysMenu> list = menuService.list();
        return ApiResult.success(menuService.buildMenuTree(list));
    }

    @Operation(summary = "根据ID获取菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public ApiResult<SysMenu> getMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return ApiResult.success(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    public ApiResult<?> addMenu(@RequestBody SysMenu menu) {
        return ApiResult.success(menuService.save(menu));
    }

    @Operation(summary = "修改菜单信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    public ApiResult<?> editMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @RequestBody SysMenu menu) {
        menu.setMenuId(id);
        return ApiResult.success(menuService.updateById(menu));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    public ApiResult<?> deleteMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.deleteMenu(id);
        return ApiResult.success();
    }
}
