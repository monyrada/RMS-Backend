package rmsbackend.specification.auth;

import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.auth.RefreshToken;
import rmsbackend.dto.auth.request.SessionFilterRequest;

public final class RefreshTokenSpecification {

    private RefreshTokenSpecification() {
    }

    public static Specification<RefreshToken> withFilters(String userId, SessionFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            var predicate = criteriaBuilder.equal(root.get("user").get("id"), userId);

            if (filter == null) {
                return predicate;
            }

            if (filter.getRevoked() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.equal(root.get("revoked"), filter.getRevoked())
                );
            }

            if (filter.getDeviceInfoContains() != null && !filter.getDeviceInfoContains().isBlank()) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("deviceInfo")),
                                "%" + filter.getDeviceInfoContains().toLowerCase() + "%"
                        )
                );
            }

            if (filter.getCreatedAfter() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), filter.getCreatedAfter())
                );
            }

            if (filter.getCreatedBefore() != null) {
                predicate = criteriaBuilder.and(
                        predicate,
                        criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), filter.getCreatedBefore())
                );
            }

            return predicate;
        };
    }
}
