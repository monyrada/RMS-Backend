package rmsbackend.repository.roles;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import rmsbackend.domain.roles.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role> {

    boolean existsByNameIgnoreCase(String name);
    Optional<Role> findByNameIgnoreCase(String name);

}
