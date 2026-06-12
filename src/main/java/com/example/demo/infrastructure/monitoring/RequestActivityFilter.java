package com.example.demo.infrastructure.monitoring;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Component
public class RequestActivityFilter extends OncePerRequestFilter {

    private final ActivityMonitoringService activityMonitoringService;

    public RequestActivityFilter(ActivityMonitoringService activityMonitoringService) {
        this.activityMonitoringService = activityMonitoringService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path == null
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/")
                || path.startsWith("/webjars/")
                || "/favicon.ico".equals(path)
                || "/error".equals(path)
                || path.startsWith("/api/admin/monitoring");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long startedAt = System.currentTimeMillis();
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        try {
            filterChain.doFilter(request, responseWrapper);
        } finally {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                activityMonitoringService.recordHttpActivity(
                        request,
                        authentication,
                        responseWrapper.getStatus(),
                        System.currentTimeMillis() - startedAt
                );
            } finally {
                responseWrapper.copyBodyToResponse();
            }
        }
    }
}