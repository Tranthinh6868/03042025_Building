package com.fitness.userservice.service;

import com.fitness.userservice.dto.RegisterRequest;
import com.fitness.userservice.dto.UserResponse;
import com.fitness.userservice.model.User;
import com.fitness.userservice.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;
    public UserResponse getUserProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        UserResponse userResponse = new UserResponse();
        userResponse.setId(user.getId());
        userResponse.setPassword(user.getPassword());
        userResponse.setEmail(user.getEmail());
        userResponse.setLastName(user.getLastName());
        userResponse.setFirstName(user.getFirstName());
        userResponse.setCreateAt(user.getCreateAt());
        userResponse.setUpdateAt(user.getUpdateAt());
        return userResponse;
    }

    public UserResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())){
            User existingUser = userRepository.findByEmail((request.getEmail()));
            UserResponse userResponse = new UserResponse();
            userResponse.setId(existingUser.getId());
            userResponse.setPassword(existingUser.getPassword());
            userResponse.setKeycloakId(existingUser.getKeycloakId());
            userResponse.setEmail(existingUser.getEmail());
            userResponse.setLastName(existingUser.getLastName());
            userResponse.setFirstName(existingUser.getFirstName());
            userResponse.setCreateAt(existingUser.getCreateAt());
            userResponse.setUpdateAt(existingUser.getUpdateAt());
            return userResponse;
        }
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setKeycloakId(request.getKeycloakId());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());
        User saveUser = userRepository.save(user);

        UserResponse userResponse = new UserResponse();
        userResponse.setId(saveUser.getId());
        userResponse.setPassword(saveUser.getPassword());
        userResponse.setKeycloakId(saveUser.getKeycloakId());
        userResponse.setEmail(saveUser.getEmail());
        userResponse.setLastName(saveUser.getLastName());
        userResponse.setFirstName(saveUser.getFirstName());
        userResponse.setCreateAt(saveUser.getCreateAt());
        userResponse.setUpdateAt(saveUser.getUpdateAt());
        return userResponse;

    }

    public Boolean existByUserId(String userId) {
        log.info("Calling User Validation API for userid: {}", userId);
        return userRepository.existsByKeycloakId(userId);
        //return userRepository.existsByEmail(userId);
        //return userRepository.existsById(userId);
        //existsByEmail
    }
}
