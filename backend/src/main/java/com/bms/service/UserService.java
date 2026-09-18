package com.bms.service;

import com.bms.dto.Dtos.LoginRequest;
import com.bms.dto.Dtos.RegisterRequest;
import com.bms.dto.Dtos.UpdateUserRequest;
import com.bms.exception.DuplicateResourceException;
import com.bms.exception.InvalidCredentialsException;
import com.bms.exception.ResourceNotFoundException;
import com.bms.model.User;
import com.bms.repository.UserRepository;
import com.bms.util.PasswordUtil;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Business logic layer. Controllers never talk to the repository directly -
 * they always go through a service. This is where rules like "the username
 * must be unique" or "the password must be hashed before saving" live.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUsername(request.getUsername());
        user.setPassword(PasswordUtil.hash(request.getPassword()));

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username/email or password"));

        if (!PasswordUtil.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username/email or password");
        }
        return user;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Integer id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User updateUser(Integer id, UpdateUserRequest request) {
        User user = getUserById(id);

        // If the email is changing, make sure the new one isn't already used by someone else
        if (!user.getEmail().equalsIgnoreCase(request.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.update(user);
        return getUserById(id);
    }

    public void deleteUser(Integer id) {
        getUserById(id); // throws ResourceNotFoundException if missing
        userRepository.deleteById(id);
    }
}
