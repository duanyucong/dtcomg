package com.dtcomg.system.security;

import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dtcomg.system.service.ISysMenuService;

import java.util.Set;

/**
 * Custom UserDetailsService to load user-specific data.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysMenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.query().eq("user_name", username).one();
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        Set<String> permissions = menuService.findPermsByUserId(user.getUserId());
        return new LoginUser(user, permissions);
    }
}
