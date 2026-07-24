package rmsbackend.common.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

/**
 * Logs request/response bodies at DEBUG level only.
 *
 * Keep this opt-in per environment because bodies can contain PII, passwords,
 * or tokens. Enable only when actively debugging:
 * logging.level.rmsbackend.common.logging.RequestBodyLoggingFilter: DEBUG
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class RequestBodyLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestBodyLoggingFilter.class);
    private static final int MAX_PAYLOAD_LENGTH = 10_000;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (!log.isDebugEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request, MAX_PAYLOAD_LENGTH);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            logPayload("Request", request, request.getContentType(), request.getCharacterEncoding(),
                    wrappedRequest.getContentAsByteArray());
            logPayload("Response", request, response.getContentType(), response.getCharacterEncoding(),
                    wrappedResponse.getContentAsByteArray());

            // ContentCachingResponseWrapper buffers internally; this writes bytes to the client.
            wrappedResponse.copyBodyToResponse();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator")
                || path.startsWith("/swagger")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || isBinaryOrMultipart(request.getContentType());
    }

    private void logPayload(String prefix,
                            HttpServletRequest request,
                            String contentType,
                            String characterEncoding,
                            byte[] payload) {
        if (payload.length == 0 || isBinaryOrMultipart(contentType)) {
            return;
        }

        String body = new String(payload, charsetOrUtf8(characterEncoding));
        if (body.isBlank()) {
            return;
        }

        String suffix = payload.length >= MAX_PAYLOAD_LENGTH ? " [truncated]" : "";
        log.debug("{} body [{} {}{}]: {}", prefix, request.getMethod(), request.getRequestURI(), suffix,
                LogMasker.mask(body));
    }

    private Charset charsetOrUtf8(String characterEncoding) {
        if (characterEncoding == null || characterEncoding.isBlank()) {
            return StandardCharsets.UTF_8;
        }

        try {
            return Charset.forName(characterEncoding);
        } catch (Exception ex) {
            return StandardCharsets.UTF_8;
        }
    }

    private boolean isBinaryOrMultipart(String contentType) {
        if (contentType == null || contentType.isBlank()) {
            return false;
        }

        String normalized = contentType.toLowerCase(Locale.ROOT);
        return normalized.startsWith("multipart/")
                || normalized.startsWith("image/")
                || normalized.startsWith("audio/")
                || normalized.startsWith("video/")
                || normalized.contains("octet-stream")
                || normalized.contains("pdf")
                || normalized.contains("zip");
    }
}
