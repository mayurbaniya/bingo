package com.hp.bingo.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AdminAuthInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        HttpSession session = request.getSession(false);

        boolean isLoggedIn = session != null &&  Boolean.TRUE.equals(session.getAttribute("isAdminLoggedIn"));

        if (!isLoggedIn) {
            response.sendRedirect("/admin/login");
            return false;
        }
        return true;
    }
}
