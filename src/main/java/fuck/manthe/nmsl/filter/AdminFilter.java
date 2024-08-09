package fuck.manthe.nmsl.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@Order(1)
@Log4j2
public class AdminFilter implements Filter {
    String adminPassword = System.getProperty("adminPassword", UUID.randomUUID().toString());

    {
        log.warn("Admin password: {}", adminPassword);
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (requestURI.startsWith("/admin/")) {
            String adminParam = httpRequest.getHeader("X-Admin-Password");

            if (adminPassword.equals(adminParam)) {
                chain.doFilter(request, response);
            } else {
                log.warn("Someone tried to log in to the dashboard, but the password was incorrect.");
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid admin parameter");
//                return;
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
