package com.dtcomg.system.security;

import com.dtcomg.system.common.ApiResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

/**
 * Handles the case where an unauthenticated user tries to access a protected resource.
 */
@Component
public class AuthenticationEntryPointImpl implements AuthenticationEntryPoint, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(401);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ApiResult<?> result = ApiResult.error(401, "请求访问：" + request.getRequestURI() + "，认证失败，无法访问系统资源");
        // Use Jackson ObjectMapper to serialize the response
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().print(mapper.writeValueAsString(result));
    }
}
