package fuck.manthe.nmsl.filter;


import fuck.manthe.nmsl.service.MaintenanceService;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class MaintenanceFilter implements Filter {
    @Resource
    MaintenanceService maintenanceService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        if (maintenanceService.isMaintaining()) {
            String path = request.getRequestURI();

            if (!path.equals("/auth.php") && !path.startsWith("/dashboard") && !path.startsWith("/admin")) {
                response.sendRedirect("/maintain");
                return;
            }
        }
        chain.doFilter(request, response);
    }
}
