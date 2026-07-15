package rmsbackend.service.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import rmsbackend.common.exception.ResourceNotFoundException;
import rmsbackend.common.generic.PaginationRequest;
import rmsbackend.common.util.PaginationUtils;
import rmsbackend.domain.users.PasswordResetToken;
import rmsbackend.domain.users.User;
import rmsbackend.dto.users.*;
import rmsbackend.mapper.UserMapper;
import rmsbackend.repository.users.PasswordResetTokenRepository;
import rmsbackend.repository.users.UserRepository;

import java.time.LocalDateTime;
import java.util.UUID;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetTokenRepository resetTokenRepository;

    @Value("${app.password-reset.expiry-minutes:15}")
    private long resetTokenExpiryMinutes;

    public UserResponse create(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
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
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
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

    /**
     * NOTE: returning the raw token to the caller is a TEMPORARY stand-in until
     * an email service exists. Replace with "send token via email" before this
     * goes anywhere near production.
     */
    public String forgetPassword(ForgetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User with email " + request.getEmail() + " not found."));

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiryDate(LocalDateTime.now().plusMinutes(resetTokenExpiryMinutes))
                .used(false)
                .build();

        resetTokenRepository.save(resetToken);
        log.info("Password reset token generated for user id {}", user.getId());

        return token;
    }

    public boolean resetPassword(ResetPasswordRequest request) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(request.getToken()).orElse(null);

        if (resetToken == null || resetToken.isUsed() || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);

        log.info("Password reset completed for user id {}", user.getId());

        return true;
    }


}
