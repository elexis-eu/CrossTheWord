package it.uniroma1.lcl.crucyservlet.utils;

import org.apache.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static it.uniroma1.lcl.crucyservlet.utils.Parameters.EMAIL;

public class AuthenticationFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) servletResponse;

        HttpSession session = req.getSession(false);

        // Check if the user is logged
        if (session == null || session.getAttribute(EMAIL) == null) {
            System.out.println("The user isn't logged");
            resp.setStatus(HttpStatus.SC_UNAUTHORIZED);
            resp.getWriter().println("Authentication is required");

            return;
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
