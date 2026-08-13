package rmsbackend.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import rmsbackend.domain.users.User;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.jwt.secret:change-this-development-secret-before-production}")
    private String jwtSecret;

    @Value("${app.jwt.access-token-expiry-minutes:15}")
    private long accessTokenExpiryMinutes;

    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(getAccessTokenTtlSeconds());

        Map<String, Object> header = new LinkedHashMap<>();
        header.put("alg", "HS256");
        header.put("typ", "JWT");

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", user.getId());
        payload.put("email", user.getEmail());
        payload.put("username", user.getUsername());
        payload.put("iat", now.getEpochSecond());
        payload.put("exp", expiresAt.getEpochSecond());

        String headerPart = encodeJson(header);
        String payloadPart = encodeJson(payload);
        String signaturePart = sign(headerPart + "." + payloadPart);

        return headerPart + "." + payloadPart + "." + signaturePart;
    }

    public String getUserId(String token) {
        Map<String, Object> payload = parsePayload(token);
        Object subject = payload.get("sub");

        if (subject == null || subject.toString().isBlank()) {
            throw new IllegalArgumentException("JWT subject is missing.");
        }

        return subject.toString();
    }

    public boolean isValid(String token) {
        String[] parts = split(token);
        String expectedSignature = sign(parts[0] + "." + parts[1]);

        if (!constantTimeEquals(expectedSignature, parts[2])) {
            return false;
        }

        Map<String, Object> payload = parsePayload(token);
        Object expiresAt = payload.get("exp");

        if (!(expiresAt instanceof Number number)) {
            return false;
        }

        return Instant.now().getEpochSecond() < number.longValue();
    }

    public long getAccessTokenTtlSeconds() {
        return accessTokenExpiryMinutes * 60;
    }

    private String encodeJson(Map<String, Object> value) {
        try {
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(objectMapper.writeValueAsBytes(value));
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Unable to serialize JWT content.", exception);
        }
    }

    private Map<String, Object> parsePayload(String token) {
        try {
            String payload = split(token)[1];
            byte[] decoded = Base64.getUrlDecoder().decode(payload);

            return objectMapper.readValue(decoded, new TypeReference<>() {
            });
        } catch (IllegalArgumentException | IOException exception) {
            throw new IllegalArgumentException("JWT token is invalid.", exception);
        }
    }

    private String sign(String value) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM));

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to sign JWT token.", exception);
        }
    }

    private String[] split(String token) {
        if (token == null) {
            throw new IllegalArgumentException("JWT token is missing.");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("JWT token is invalid.");
        }

        return parts;
    }

    private boolean constantTimeEquals(String expected, String actual) {
        byte[] expectedBytes = expected.getBytes(StandardCharsets.UTF_8);
        byte[] actualBytes = actual.getBytes(StandardCharsets.UTF_8);

        if (expectedBytes.length != actualBytes.length) {
            return false;
        }

        int result = 0;
        for (int index = 0; index < expectedBytes.length; index++) {
            result |= expectedBytes[index] ^ actualBytes[index];
        }

        return result == 0;
    }
}
