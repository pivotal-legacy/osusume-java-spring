package com.tokyo.beach.restaurants.filter;

import com.tokyo.beach.restaurants.session.SessionDataMapper;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class AuthorizationValidator {
    private SessionDataMapper sessionDataMapper;

    public AuthorizationValidator(SessionDataMapper sessionDataMapper) {
        this.sessionDataMapper = sessionDataMapper;
    }

    public boolean authorizeRequest(ServletRequest request) throws IOException, ServletException {
        String servletName = ((HttpServletRequest) request).getServletPath();
        String method = ((HttpServletRequest)request).getMethod();

        if (servletName.equals("/unauthenticated") || servletName.equals("/session") || method.equalsIgnoreCase("options")) {
            return true;
        }

        String originalToken = ((HttpServletRequest) request).getHeader("Authorization");
        String token = originalToken;
        if (originalToken == null) {
            return false;
        }

        if (originalToken.contains("Bearer")) {
            token = originalToken.replace("Bearer", "").trim();
        }

        Optional<Long> maybeUserId = this.sessionDataMapper.validateToken(token);

        if (maybeUserId.isPresent()) {
            Long userId = maybeUserId.get();
            request.setAttribute("userId", userId);
            return true;
        } else {
            return false;
        }
    }
}
