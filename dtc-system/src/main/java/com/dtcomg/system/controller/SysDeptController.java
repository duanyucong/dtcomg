package com.dtcomg.system.controller;

import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.domain.SysDept;
import com.dtcomg.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/depts")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    public ApiResult<List<SysDept>> list() {
        // In a real implementation, a service method would build the tree structure.
        List<SysDept> list = deptService.list();
        // TODO: Implement tree building logic.
        return ApiResult.success(list);
    }

    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    public ApiResult<SysDept> getDept(@PathVariable Long id) {
        return ApiResult.success(deptService.getById(id));
    }

    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    public ApiResult<?> addDept(@RequestBody SysDept dept) {
        return ApiResult.success(deptService.save(dept));
    }

    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    public ApiResult<?> editDept(@PathVariable Long id, @RequestBody SysDept dept) {
        dept.setDeptId(id);
        return ApiResult.success(deptService.updateById(dept));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    public ApiResult<?> deleteDept(@PathVariable Long id) {
        // Note: Deleting a parent dept should also handle its children.
        return ApiResult.success(deptService.removeById(id));
    }
}
