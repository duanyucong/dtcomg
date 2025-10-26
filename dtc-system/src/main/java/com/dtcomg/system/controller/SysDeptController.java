package com.dtcomg.system.controller;

import com.dtcomg.system.common.Result;
import com.dtcomg.system.domain.SysDept;
import com.dtcomg.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "部门管理")
@RestController
@RequestMapping("/api/depts")
public class SysDeptController {

    @Autowired
    private ISysDeptService deptService;

    @Operation(summary = "获取部门列表（树形）")
    @GetMapping
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    public Result<List<SysDept>> list() {
        List<SysDept> list = deptService.list();
        return Result.success(deptService.buildDeptTree(list));
    }

    @Operation(summary = "根据ID获取部门详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    public Result<SysDept> getDept(@Parameter(description = "部门ID") @PathVariable Long id) {
        return Result.success(deptService.getById(id));
    }

    @Operation(summary = "新增部门")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    public Result<?> addDept(@RequestBody SysDept dept) {
        return Result.success(deptService.save(dept));
    }

    @Operation(summary = "修改部门信息")
    @PutMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    public Result<?> editDept(
            @Parameter(description = "部门ID") @PathVariable Long id,
            @RequestBody SysDept dept) {
        dept.setDeptId(id);
        return Result.success(deptService.updateById(dept));
    }

    @Operation(summary = "删除部门")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    public Result<?> deleteDept(@Parameter(description = "部门ID") @PathVariable Long id) {
        deptService.deleteDept(id);
        return Result.success();
    }
}