package com.tokyo.beach.restaurants.filter;

import com.tokyo.beach.restaurants.session.SessionRepository;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Optional;

public class AuthorizationValidator {
    private SessionRepository sessionRepository;

    public AuthorizationValidator(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public boolean authorizeRequest(ServletRequest request) throws IOException, ServletException {
        String servletName = ((HttpServletRequest) request).getServletPath();

        if (servletName.equals("/unauthenticated") || servletName.equals("/session")) {
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

        Optional<Long> maybeUserId = this.sessionRepository.validateToken(token);

        if (maybeUserId.isPresent()) {
            Long userId = maybeUserId.get();
            request.setAttribute("userId", userId);
            return true;
        } else {
            return false;
        }
    }
}
