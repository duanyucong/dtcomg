package com.dtcomg.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.dtcomg.system.domain.SysDept;

import java.util.List;

/**
 * Department Service interface.
 */
public interface ISysDeptService extends IService<SysDept> {

    /**
     * 构建部门树
     * @param depts 部门列表
     * @return 树形结构的部门列表
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 是否存在子节点
     * @param deptId 部门ID
     * @return 结果
     */
    boolean hasChild(Long deptId);

    /**
     * 删除部门
     * @param deptId 部门ID
     */
    void deleteDept(Long deptId);
}
