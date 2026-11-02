package rmsbackend.repository.permissions;

import org.springframework.data.jpa.repository.JpaRepository;
import rmsbackend.domain.permissions.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PermissionRepository extends JpaRepository<Permission, String> {

    boolean existsByCode(String code);
    Optional<Permission> findByCode(String code);
    List<Permission> findByCodeIn(Collection<String> codes);

}
