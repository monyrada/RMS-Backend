package rmsbackend.service.roles;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.roles.Role;
import rmsbackend.dto.roles.RoleRequest;
import rmsbackend.dto.roles.RoleResponse;
import rmsbackend.mapper.RoleMapper;
import rmsbackend.repository.roles.RoleRepository;
import rmsbackend.specification.RoleSpecification;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    /**
     * Creates a new role.
     * Rejects the request if a role with the same name (case-insensitive) already exists,
     * since `name` is expected to be unique.
     */
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        Role role = Role.builder()
                .name(request.getName())
                .description(request.getDescription())
                .enabled(request.getEnabled())
                .build();

        roleRepository.save(role);
        log.info("Role id {} has been created on date {}", role.getId(), role.getCreatedAt());

        return roleMapper.toResponse(role);
    }

    /**
     * Updates an existing role by id.
     * Only re-checks name uniqueness if the name is actually changing, so updating
     * a role without touching its name never trips the duplicate-name check.
     */
    public RoleResponse update(String id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with ID: " + id));

        if (request.getName() != null
                && !request.getName().equalsIgnoreCase(role.getName())
                && roleRepository.existsByNameIgnoreCase(request.getName())) {
            throw new IllegalArgumentException("Role with name '" + request.getName() + "' already exists");
        }

        role.setName(request.getName());
        role.setDescription(request.getDescription());
        role.setEnabled(request.getEnabled());

        roleRepository.save(role);
        log.info("Role id {} has been updated", role.getId());

        return roleMapper.toResponse(role);
    }

    /**
     * Returns a paginated, optionally filtered list of roles.
     * `name` does a partial, case-insensitive match; `enabled` is an exact match.
     * Both filters are optional — pass null to skip either one (see RoleSpecification).
     */
    public Page<RoleResponse> getAllRoles(String name, Boolean enabled, PaginationRequest pagination) {
        log.info("====== Fetching roles include pagination =====");

        Pageable pageable = PaginationUtils.pageable(pagination);
        Specification<Role> spec = RoleSpecification.filterBy(name, enabled);
        Page<Role> roles = roleRepository.findAll(spec, pageable);

        return roles.map(roleMapper::toResponse);
    }

    /**
     * Fetches a single role by id, or throws if no role exists with that id.
     */
    public RoleResponse getRoleById(String id) {
        var role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role with id " + id + " not found"));

        log.info("Retrieved role by id: {}", role.getId());
        return roleMapper.toResponse(role);
    }

    /**
     * Deletes a role by id.
     * Returns false (instead of throwing) when the role doesn't exist, so the
     * controller can respond with a plain 404 rather than an exception.
     */
    public boolean delete(String id) {
        if (!roleRepository.existsById(id)) {
            return false;
        }

        roleRepository.deleteById(id);
        log.info("Role id {} has been deleted", id);

        return true;
    }
}