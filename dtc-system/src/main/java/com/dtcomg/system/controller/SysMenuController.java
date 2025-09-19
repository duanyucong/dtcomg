package com.dtcomg.system.controller;

import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.service.ISysMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/menus")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    public ApiResult<List<SysMenu>> list() {
        // In a real implementation, a service method would build the tree structure.
        List<SysMenu> list = menuService.list();
        // TODO: Implement tree building logic.
        return ApiResult.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    public ApiResult<SysMenu> getMenu(@PathVariable Long id) {
        return ApiResult.success(menuService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    public ApiResult<?> addMenu(@RequestBody SysMenu menu) {
        return ApiResult.success(menuService.save(menu));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    public ApiResult<?> editMenu(@PathVariable Long id, @RequestBody SysMenu menu) {
        menu.setMenuId(id);
        return ApiResult.success(menuService.updateById(menu));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    public ApiResult<?> deleteMenu(@PathVariable Long id) {
        // Note: Deleting a parent menu should also handle its children.
        // This logic should be in the service layer.
        return ApiResult.success(menuService.removeById(id));
    }
}
