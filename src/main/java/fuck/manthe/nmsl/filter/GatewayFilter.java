package fuck.manthe.nmsl.filter;

import fuck.manthe.nmsl.service.GatewayService;
import fuck.manthe.nmsl.service.impl.GatewayServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Log4j2
@Order(2)
public class GatewayFilter implements Filter {
    @Value("${service.gateway.key}")
    String gatewayKey;
    @Value("${service.gateway.always}")
    boolean alwaysEnableGateway;

    @Resource
    GatewayService gatewayService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String requestURI = httpRequest.getRequestURI();

        if (requestURI.startsWith("/gateway/")) {
            if (!gatewayService.isPureGateway() && !alwaysEnableGateway) {
                httpResponse.sendError(HttpServletResponse.SC_BAD_REQUEST, "Gateway is not enabled in config");
                return;
            }
            String providedKey = httpRequest.getHeader("X-Gateway-Key");
            if (!gatewayKey.equals(providedKey)) {
                log.warn("Invalid gateway key provided. Please ensure that the gateway key in the JVM parameters matches the one in the dashboard");
                httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid gateway key");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
