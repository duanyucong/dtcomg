package com.dtcomg.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dtcomg.system.domain.SysMenu;
import com.dtcomg.system.mapper.SysMenuMapper;
import com.dtcomg.system.service.ISysMenuService;
import org.springframework.stereotype.Service;

/**
 * Menu Service implementation.
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements ISysMenuService {
}
