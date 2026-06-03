package rmsbackend.common.util;

import org.springframework.data.domain.Page;
import rmsbackend.common.generic.StatusCode;
import rmsbackend.dto.RespondDTO;

import java.util.Collection;

/**
 * Utility class for formatting standardized API responses.
 * Response Structure:
 * Supports:
 * - Single Object
 * - Collection/List
 * - Spring Data Page (Pagination)
 * - Error Response
 */
public final class JSONRespond {

    /**
     * Prevent instantiation of utility class.
     */
    private JSONRespond() {
    }

    /**
     * Generate success response using default SUCCESS status.
     *
     * @param resource Response data
     * @return Standard response DTO
     */
    public static RespondDTO respond(Object resource) {
        return respond(resource, StatusCode.SUCCESS);
    }

    /**
     * Generate success response using provided status code.
     *
     * @param resource   Response data
     * @param statusCode Response status code
     * @return Standard response DTO
     */
    public static RespondDTO respond(Object resource, StatusCode statusCode) {
        return respond(
                resource,
                statusCode,
                statusCode.getMessage()
        );
    }

    /**
     * Generate standard response.
     * Supported data types:
     * - Page      => total = total elements, data = current page content
     * - Collection => total = collection size
     * - Object     => total = 1
     * - null       => total = 0
     *
     * @param resource   Response data
     * @param statusCode Response status code
     * @param message    Custom response message
     * @return Standard response DTO
     */
    public static RespondDTO respond(Object resource, StatusCode statusCode, String message) {
        int total = 0;
        Object data = resource;

        // Handle Spring Data pagination response
        if (resource instanceof Page<?> page) {

            // Total records in database
            total = (int) page.getTotalElements();

            // Current page content only
            data = page.getContent();

        }
        // Handle List, Set, Collection
        else if (resource instanceof Collection<?> collection) {
            total = collection.size();

        }
        // Handle single object
        else if (resource != null) {
            total = 1;
        }

        return RespondDTO.builder()
                .statusCode(statusCode.getCode())
                .data(data)
                .total(total)
                .message(message)
                .error(null)
                .build();
    }

    /**
     * Generate error response.
     *
     * Example:
     * {
     *   "statusCode": "404",
     *   "data": null,
     *   "total": 0,
     *   "message": "Resource Not Found",
     *   "error": "Category not found"
     * }
     *
     * @param statusCode Error status code
     * @param error      Error details
     * @return Error response DTO
     */
    public static RespondDTO error(
            StatusCode statusCode,
            Object error
    ) {

        return RespondDTO.builder()
                .statusCode(statusCode.getCode())
                .message(statusCode.getMessage())
                .data(null)
                .total(0)
                .error(error)
                .build();
    }
}