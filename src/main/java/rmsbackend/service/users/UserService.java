package rmsbackend.service.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.users.User;
import rmsbackend.dto.users.ChangePasswordRequest;
import rmsbackend.dto.users.UserRequest;
import rmsbackend.dto.users.UserResponse;
import rmsbackend.mapper.UserMapper;
import rmsbackend.repository.users.PasswordResetTokenRepository;
import rmsbackend.repository.users.UserRepository;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    public UserResponse create(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .profileImage(request.getProfileImage())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .enabled(request.getEnabled())
                .status(request.getStatus())
                .build();

        userRepository.save(user);
        log.info("User id {} has been created on date {}", user.getId(), user.getCreatedAt());

        return userMapper.toResponse(user);
    }

    public UserResponse update(String id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        user.setUsername(request.getUsername());
        user.setFirstName(request.getFirstname());
        user.setLastName(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setProfileImage(request.getProfileImage());
        user.setGender(request.getGender());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setEnabled(request.getEnabled());
        user.setStatus(request.getStatus());

        userRepository.save(user);
        log.info("User id {} has been updated", user.getId());

        return userMapper.toResponse(user);
    }

    public Page<UserResponse> getAllUsers(PaginationRequest pagination) {
        log.info("====== Fetching users include pagination =====");

        Pageable pageable = PaginationUtils.pageable(pagination);
        Page<User> users = userRepository.findAll(pageable);

        return users.map(userMapper::toResponse);
    }

    public UserResponse getUserById(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + id + " not found"));

        log.info( "Retrieved user by id: {}", user.getId());
        return userMapper.toResponse(user);
    }

    public boolean delete(String id) {
        if (!userRepository.existsById(id)) {
            return false;
        }

        userRepository.deleteById(id);
        log.info("User id {} has been deleted", id);

        return true;
    }

    /**
     * TODO: userId should come from the authenticated principal (SecurityContextHolder),
     * not a caller-supplied path variable, once the JWT auth layer exposes it here.
     */
    public boolean changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with id "+ userId + " not found."));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect.");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("Password changed for user id {}", userId);

        return true;
    }

}
