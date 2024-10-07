package fuck.manthe.nmsl.filter;

import fuck.manthe.nmsl.entity.VapeRestBean;
import fuck.manthe.nmsl.service.OnlineConfigService;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
@Order(2)
public class OnlineFilter implements Filter {
    @Resource
    OnlineConfigService service;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/v1/")) {
            String[] split = requestURI.split("/");
            if (split.length <= 4) {
                response.sendRedirect("/");
                return;
            }

            String token = split[3];
            if (service.findByToken(token) == null) {
                response.setContentType("application/json; charset=utf-8");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(VapeRestBean.failedToFindAccount().toJson());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
