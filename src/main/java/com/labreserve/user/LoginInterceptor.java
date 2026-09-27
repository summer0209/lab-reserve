package com.labreserve.user;

import com.labreserve.common.BizException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String path = request.getRequestURI();
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if ("/api/health".equals(path)
                || "/api/auth/login".equals(path)
                || "/api/auth/register".equals(path)) {
            return true;
        }
        if (request.getSession(false) == null
                || request.getSession(false).getAttribute(CurrentUser.ID) == null) {
            throw new BizException("请先登录");
        }
        return true;
    }
}
