package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysDept;
import com.dtcomg.system.mapper.SysDeptMapper;
import com.dtcomg.system.service.ISysDeptService;
import org.springframework.stereotype.Service;

/**
 * Department Service implementation.
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements ISysDeptService {
}
