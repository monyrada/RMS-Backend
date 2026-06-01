package rmsbackend.common.generic.response;

import java.time.Instant;

public class ResponseBuilder {

    private ResponseBuilder() {}

    public static <T> ApiResponse<T> respond(StatusCode statusCode, T data) {
        return ApiResponse.<T>builder()
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .success(true)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> respond(StatusCode statusCode, String message) {
        return ApiResponse.<T>builder()
                .code(statusCode.getCode())
                .message(message)
                .success(false)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(StatusCode statusCode) {
        return ApiResponse.<Void>builder()
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .success(false)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

    public static ApiResponse<Void> error(StatusCode statusCode, String message) {
        return ApiResponse.<Void>builder()
                .code(statusCode.getCode())
                .message(statusCode.getMessage())
                .success(false)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }

}
