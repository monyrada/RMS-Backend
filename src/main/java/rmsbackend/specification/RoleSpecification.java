package rmsbackend.specification;

import org.springframework.data.jpa.domain.Specification;
import rmsbackend.domain.roles.Role;

public class RoleSpecification {

    public static Specification<Role> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Role> isEnabled(Boolean enabled) {
        return (root, query, cb) ->
                enabled == null ? null : cb.equal(root.get("enabled"), enabled);
    }

    public static Specification<Role> filterBy(String name, Boolean enabled) {
        return Specification.where(hasName(name)).and(isEnabled(enabled));
    }

}
