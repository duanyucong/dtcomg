package com.dtcomg.system.controller;

import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.security.JwtUtils;
import com.dtcomg.system.security.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.dtcomg.system.service.ISysRoleService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "用户认证")
@RestController
@RequestMapping("/api")
public class SysLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private ISysRoleService roleService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ApiResult<?> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String token = jwtUtils.generateToken(loginUser);

        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);

        return ApiResult.success(tokenMap);
    }

    @Operation(summary = "获取用户信息")
    @GetMapping("/getInfo")
    public ApiResult<?> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();

        // Get roles and permissions
        Set<String> roles = roleService.findRolesByUserId(user.getUserId());
        Set<String> permissions = loginUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("permissions", permissions);
        data.put("roles", roles);

        return ApiResult.success(data);
    }

    @Operation(summary = "用户登出")
    @PostMapping("/logout")
    public ApiResult<?> logout() {
        return ApiResult.success("登出成功");
    }

    // Inner class for login request body
    static class LoginRequest {
        private String username;
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
