package rmsbackend.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

/**
 * Logs request method/path and response status/duration at INFO level.
 * Trace/span IDs are populated by Micrometer Tracing and rendered by logback.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";
    private static final String REQUEST_ID_MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        long start = System.nanoTime();
        String requestId = resolveRequestId(request);
        MDC.put(REQUEST_ID_MDC_KEY, requestId);
        response.setHeader(REQUEST_ID_HEADER, requestId);

        try {
            log.info("===== {} {} clientIp={} browser=\"{}\" origin=\"{}\" referer=\"{}\" contentType=\"{}\" contentLength={}",
                    request.getMethod(),
                    fullPath(request),
                    clientIp(request),
                    browser(request),
                    headerOrDash(request, "Origin"),
                    headerOrDash(request, "Referer"),
                    valueOrDash(request.getContentType()),
                    request.getContentLengthLong());

            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            log.info("===== {} {} status={} outcome={} durationMs={} responseType=\"{}\"",
                    request.getMethod(),
                    fullPath(request),
                    response.getStatus(),
                    outcome(response.getStatus()),
                    durationMs,
                    valueOrDash(response.getContentType()));
            MDC.remove(REQUEST_ID_MDC_KEY);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs");
    }

    private String resolveRequestId(HttpServletRequest request) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            return UUID.randomUUID().toString();
        }
        return oneLine(requestId);
    }

    private String fullPath(HttpServletRequest request) {
        String query = request.getQueryString();
        if (query == null || query.isBlank()) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + query;
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return oneLine(forwardedFor.split(",")[0].trim());
        }

        String realIp = request.getHeader("X-Real-IP");
        if (realIp != null && !realIp.isBlank()) {
            return oneLine(realIp);
        }

        return valueOrDash(request.getRemoteAddr());
    }

    private String browser(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null || userAgent.isBlank()) {
            return "-";
        }

        String normalized = userAgent.toLowerCase(Locale.ROOT);
        if (normalized.contains("edg/")) {
            return "Edge";
        }
        if (normalized.contains("chrome/") && !normalized.contains("chromium")) {
            return "Chrome";
        }
        if (normalized.contains("firefox/")) {
            return "Firefox";
        }
        if (normalized.contains("safari/") && normalized.contains("version/")) {
            return "Safari";
        }
        if (normalized.contains("postmanruntime")) {
            return "Postman";
        }
        if (normalized.contains("curl/")) {
            return "curl";
        }
        return oneLine(userAgent);
    }

    private String outcome(int status) {
        if (status >= 500) {
            return "SERVER_ERROR";
        }
        if (status >= 400) {
            return "CLIENT_ERROR";
        }
        if (status >= 300) {
            return "REDIRECTION";
        }
        return "SUCCESS";
    }

    private String headerOrDash(HttpServletRequest request, String name) {
        return valueOrDash(request.getHeader(name));
    }

    private String valueOrDash(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return oneLine(value);
    }

    private String oneLine(String value) {
        return value.replaceAll("[\\r\\n\\t]+", " ").trim();
    }
}
