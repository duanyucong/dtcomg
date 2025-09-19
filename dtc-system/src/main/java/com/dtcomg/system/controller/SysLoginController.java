package com.dtcomg.system.controller;

import com.dtcomg.system.common.ApiResult;
import com.dtcomg.system.domain.SysUser;
import com.dtcomg.system.security.JwtUtils;
import com.dtcomg.system.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SysLoginController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

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

    @GetMapping("/getInfo")
    public ApiResult<?> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        SysUser user = loginUser.getUser();

        // TODO: Get roles and permissions
        Map<String, Object> data = new HashMap<>();
        data.put("user", user);
        data.put("permissions", loginUser.getAuthorities()); // This is currently null
        data.put("roles", null); // TODO

        return ApiResult.success(data);
    }

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
