package rmsbackend.service.permissions;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import rmsbackend.dto.permissions.PermissionResponse;
import rmsbackend.mapper.PermissionMapper;
import rmsbackend.repository.permissions.PermissionRepository;

import java.util.List;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Returns every permission, ordered by code, so the admin UI can show what a role can be granted.
     */
    public List<PermissionResponse> getAll() {
        log.info("====== Fetching permissions =====");

        return permissionRepository.findAll(Sort.by("code")).stream()
                .map(permissionMapper::toResponse)
                .toList();
    }
}
