package fuck.manthe.nmsl.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(0)
public class InjectFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestURI().equals("/auth.php") && !(httpRequest.getRemoteHost().equals("www.vape.gg") || httpRequest.getRemoteHost().equals("vape.gg"))) {
            httpResponse.sendError(HttpServletResponse.SC_GONE, "Invalid host");
        }

        filterChain.doFilter(request, response);
    }
}
