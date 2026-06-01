package rmsbackend.common.generic;

import lombok.Getter;

@Getter
public enum StatusCode {

    SUCCESS("200", "Success"),
    CREATED("201", "Created Successfully"),
    UPDATED("200", "Updated Successfully"),
    DELETED("200", "Deleted Successfully"),

    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    FORBIDDEN("403", "Forbidden"),
    NOT_FOUND("404", "Resource Not Found"),
    RATE_LIMIT("429", "Too Many Requests"),

    INTERNAL_ERROR("500", "Internal Server Error");

    private final String code;
    private final String message;

    StatusCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
