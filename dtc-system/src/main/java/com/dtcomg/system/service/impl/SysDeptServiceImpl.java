package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysDept;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.mapper.SysDeptMapper;
import com.dtcomg.system.mapper.SysUserMapper;
import com.dtcomg.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.dtcomg.system.common.ServiceException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Department Service implementation.
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {

    @Autowired
    private SysUserMapper userMapper;

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> returnList = new ArrayList<>();
        List<Long> tempList = depts.stream().map(SysDept::getDeptId).collect(Collectors.toList());

        for (Iterator<SysDept> iterator = depts.iterator(); iterator.hasNext(); ) {
            SysDept dept = iterator.next();
            // 如果是顶级节点, 遍历该父节点的所有子节点
            if (!tempList.contains(dept.getParentId())) {
                recursionFn(depts, dept);
                returnList.add(dept);
            }
        }
        if (returnList.isEmpty()) {
            returnList = depts;
        }
        return returnList;
    }

    @Override
    public boolean hasChild(Long deptId) {
        return count(new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, deptId)) > 0;
    }

    @Override
    public void deleteDept(Long deptId) {
        // 检查是否存在子部门
        if (hasChild(deptId)) {
            throw new ServiceException("存在子部门,不允许删除");
        }
        
        // 检查部门是否被用户使用
        if (userMapper.selectCount(new LambdaQueryWrapper<SysUser>().eq(SysUser::getDeptId, deptId)) > 0) {
            throw new ServiceException("部门已被用户使用,不允许删除");
        }
        
        // 逻辑删除，使用LambdaUpdateWrapper直接更新del_flag为'2'
        LambdaUpdateWrapper<SysDept> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(SysDept::getDeptId, deptId).set(SysDept::getDelFlag, "2");
        update(updateWrapper);
    }

    /**
     * 递归列表
     */
    private void recursionFn(List<SysDept> list, SysDept t) {
        // 得到子节点列表
        List<SysDept> childList = getChildList(list, t);
        t.setChildren(childList);
        for (SysDept tChild : childList) {
            if (hasChild(list, tChild)) {
                recursionFn(list, tChild);
            }
        }
    }

    /**
     * 得到子节点列表
     */
    private List<SysDept> getChildList(List<SysDept> list, SysDept t) {
        List<SysDept> tlist = new ArrayList<>();
        Iterator<SysDept> it = list.iterator();
        while (it.hasNext()) {
            SysDept n = it.next();
            if (n.getParentId().longValue() == t.getDeptId().longValue()) {
                tlist.add(n);
            }
        }
        return tlist;
    }

    /**
     * 判断是否有子节点
     */
    private boolean hasChild(List<SysDept> list, SysDept t) {
        return !getChildList(list, t).isEmpty();
    }
}
