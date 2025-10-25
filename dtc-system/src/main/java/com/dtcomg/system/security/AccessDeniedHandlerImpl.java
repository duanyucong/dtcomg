package com.dtcomg.system.security;

import com.dtcomg.system.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;

/**
 * Handles the case where an authenticated user tries to access a resource without sufficient permissions.
 */
@Component
public class AccessDeniedHandlerImpl implements AccessDeniedHandler, Serializable {
    private static final long serialVersionUID = 1L;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(403);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        Result<?> result = Result.error(403, "没有权限，禁止访问");
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().print(mapper.writeValueAsString(result));
    }
}
