package com.dtcomg.system.controller;

import com.dtcomg.system.common.Result;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.service.ISysMenuService;
import com.dtcomg.system.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public Result<List<SysMenu>> list() {
        List<SysMenu> menus = menuService.list();
        return Result.success(menuService.buildMenuTree(menus));
    }

    @Operation(summary = "获取导航菜单（仅正常状态）")
    @GetMapping("/nav")
    public Result<List<SysMenu>> getNavMenus(@AuthenticationPrincipal LoginUser loginUser) {
        Long userId = loginUser.getUser().getUserId();
        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        return Result.success(menus);
    }

    @Operation(summary = "根据ID获取菜单详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public Result<SysMenu> getMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        return Result.success(menuService.getById(id));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    public Result<?> addMenu(@RequestBody SysMenu menu) {
        return Result.success(menuService.save(menu));
    }

    @Operation(summary = "修改菜单信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    public Result<?> editMenu(
            @Parameter(description = "菜单ID") @PathVariable Long id,
            @RequestBody SysMenu menu) {
        menu.setMenuId(id);
        return Result.success(menuService.updateById(menu));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    public Result<?> deleteMenu(@Parameter(description = "菜单ID") @PathVariable Long id) {
        menuService.deleteMenu(id);
        return Result.success();
    }
}
