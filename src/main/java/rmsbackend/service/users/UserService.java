package rmsbackend.service.users;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rmsbackend.domain.users.User;
import rmsbackend.dto.users.UserRequest;
import rmsbackend.dto.users.UserResponse;
import rmsbackend.mapper.UserMapper;
import rmsbackend.repository.users.UserRepository;

@Transactional
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse create(UserRequest request) {
        User user = User.builder()
                .username(request.getUsername())
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
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
}
