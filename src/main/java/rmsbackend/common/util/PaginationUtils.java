package rmsbackend.common.util;

import org.springframework.data.domain.*;
import rmsbackend.common.generic.PaginationRequest;

public final class PaginationUtils {

    private PaginationUtils() {
    }

    public static Pageable pageable(PaginationRequest request) {

        int page = request.getOffset() / request.getMax();

        Sort.Direction direction =
                "desc".equalsIgnoreCase(request.getOrder())
                        ? Sort.Direction.DESC
                        : Sort.Direction.ASC;

        return PageRequest.of(
                page,
                request.getMax(),
                Sort.by(direction, request.getSort())
        );
    }
}
