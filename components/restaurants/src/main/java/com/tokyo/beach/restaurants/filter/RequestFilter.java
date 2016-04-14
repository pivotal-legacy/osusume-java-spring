package com.tokyo.beach.restaurants.filter;

import com.tokyo.beach.restaurants.session.SessionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import java.io.IOException;

@Component
public class RequestFilter implements Filter {

    @Autowired
    private SessionRepository sessionRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        ApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(filterConfig.getServletContext());
        this.sessionRepository = ctx.getBean(SessionRepository.class);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        AuthorizationValidator authorizationValidator = new AuthorizationValidator(sessionRepository);

        if (authorizationValidator.authorizeRequest(request)) {
            chain.doFilter(request, response);
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/unauthenticated");
            rd.forward(request, response);
        }
    }

    @Override
    public void destroy() {

    }
}
