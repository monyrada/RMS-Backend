package rmsbackend.common.logging;

import java.util.Set;
import java.util.regex.Pattern;

/**
 * Redacts sensitive JSON fields before they are written to logs.
 */
public final class LogMasker {

    private static final Set<String> SENSITIVE_FIELDS = Set.of(
            "password",
            "oldPassword",
            "newPassword",
            "confirmPassword",
            "accessToken",
            "refreshToken",
            "token",
            "cardNumber",
            "cvv"
    );
    private static final String MASK = "***MASKED***";

    private LogMasker() {
    }

    public static String mask(String json) {
        if (json == null || json.isBlank()) {
            return json;
        }

        String masked = json;
        for (String field : SENSITIVE_FIELDS) {
            masked = masked.replaceAll(
                    "(?i)\"" + Pattern.quote(field) + "\"\\s*:\\s*\"[^\"]*\"",
                    "\"" + field + "\":\"" + MASK + "\""
            );
        }
        return masked;
    }
}
