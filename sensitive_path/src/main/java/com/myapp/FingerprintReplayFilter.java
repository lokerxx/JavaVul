package com.myapp;

import java.io.IOException;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class FingerprintReplayFilter extends OncePerRequestFilter {
    private final FingerprintCatalogService fingerprintCatalogService;

    public FingerprintReplayFilter(FingerprintCatalogService fingerprintCatalogService) {
        this.fingerprintCatalogService = fingerprintCatalogService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String method = request.getMethod();
        if (!"GET".equalsIgnoreCase(method) && !"HEAD".equalsIgnoreCase(method)) {
            return true;
        }

        String path = normalize(request.getRequestURI());
        return "/".equals(path)
            || "/index.html".equals(path)
            || "/fingerprint".equals(path)
            || "/sensitive-path".equals(path)
            || "/error".equals(path)
            || path.startsWith("/fingerprint/api/");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
        String path = normalize(request.getRequestURI());
        FingerprintRouteResponse routeResponse = fingerprintCatalogService.buildRouteResponse(path);
        if (routeResponse == null) {
            filterChain.doFilter(request, response);
            return;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(routeResponse.getContentType());
        response.setCharacterEncoding("UTF-8");
        for (Map.Entry<String, String> entry : routeResponse.getHeaders().entrySet()) {
            response.setHeader(entry.getKey(), entry.getValue());
        }
        response.setHeader("X-Fingerprint-Replay", "sqlite-route");
        response.setContentLength(routeResponse.getBody().length);
        if (!"HEAD".equalsIgnoreCase(request.getMethod())) {
            response.getOutputStream().write(routeResponse.getBody());
        }
    }

    private String normalize(String uri) {
        if (uri == null || uri.trim().isEmpty()) {
            return "/";
        }
        return uri.startsWith("/") ? uri : "/" + uri;
    }
}
